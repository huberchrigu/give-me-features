package ch.chrigu.gmf.plugins

import ch.chrigu.gfm.plugin.ItemDefinition
import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gmf.plugins.forms.PluginFormFactory
import ch.chrigu.gmf.plugins.mongo.PluginStatusRepository
import ch.chrigu.gmf.shared.aggregates.AggregateNotFoundException
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PluginService(
    private val pluginStatusRepository: PluginStatusRepository,
    private val pluginFormFactory: PluginFormFactory,
    private val plugins: List<Plugin> = emptyList()
) {
    @PostConstruct
    fun init() {
        runBlocking {
            val current = pluginStatusRepository.findAll().toList()
            val removed = current minusPlugins plugins
            val newStatus = (plugins minusPluginStatus current).map { PluginStatus.from(it) }
            val updatedStatus = current.mapNotNull { updatePluginData(it) }
            pluginStatusRepository.saveAll(newStatus + updatedStatus).toList()
            pluginStatusRepository.deleteAll(removed)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    suspend fun activatePlugin(id: PluginStatusId, version: Long) = doWith(id) { activate(version) }

    @PreAuthorize("hasRole('ADMIN')")
    suspend fun deactivatePlugin(id: PluginStatusId, version: Long) = doWith(id) { deactivate(version) }

    @PreAuthorize("hasRole('ADMIN')")
    fun findAll(): Flow<PluginStatus> = pluginStatusRepository.findAll()

    @PreAuthorize("hasRole('USER')")
    suspend fun <PARENT> update(
        entity: PARENT,
        pluginId: PluginStatusId,
        pluginData: Map<String, Any?>,
        getItemDefinition: (Plugin) -> ItemDefinition<PARENT, *>?
    ): PluginForm<*> {
        val plugin = resolvePluginDefinition(pluginId)
        val itemDefinition = getItemDefinition(plugin) ?: throw AggregateNotFoundException("Item definition for plugin $pluginId not found")
        val form = pluginFormFactory.create(itemDefinition, pluginData, TODO())
        // TODO: Persist
        return form
    }

    @PreAuthorize("hasRole('USER')")
    suspend fun <PARENT> getForms(entity: PARENT, getItemDefinition: (Plugin) -> ItemDefinition<PARENT, *>?): List<PluginForm<*>> {
        val activePlugins = pluginStatusRepository.findByActive(true).map { it.metadata.pluginId }.toList()
        val resolvedPlugins = plugins.filter { activePlugins.contains(it.id) }
        val forms = resolvedPlugins
            .mapNotNull { getItemDefinition(it) }
            .map {
                pluginFormFactory.create(it, emptyMap(), TODO()) // TODO: Data form repository
            }
        return forms
    }

    private suspend fun resolvePluginDefinition(pluginId: PluginStatusId): Plugin {
        val status = pluginStatusRepository.findById(pluginId.toString()) ?: throw AggregateNotFoundException("Plugin $pluginId not found")
        require(status.active)
        return plugins.first { it.id == pluginId } // TODO: Check conversion
    }

    private suspend fun doWith(id: PluginStatusId, apply: PluginStatus.() -> PluginStatus) = pluginStatusRepository.findById(id.toString())?.apply()?.let {
        pluginStatusRepository.save(it)
    }

    private infix fun List<Plugin>.minusPluginStatus(other: List<PluginStatus>): List<Plugin> {
        val otherIds = other.map { it.metadata.pluginId }
        return filter { !otherIds.contains(it.id) }
    }

    private infix fun List<PluginStatus>.minusPlugins(other: List<Plugin>): List<PluginStatus> {
        val otherIds = other.map { it.id }
        return filter { !otherIds.contains(it.metadata.pluginId) }
    }

    private fun updatePluginData(status: PluginStatus) = plugins.firstOrNull { it.id == status.metadata.pluginId }
        ?.let { status.updateWith(it) }
}
