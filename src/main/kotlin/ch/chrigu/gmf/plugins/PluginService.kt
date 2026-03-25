package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.plugins.forms.PluginFormFactory
import ch.chrigu.gmf.plugins.mongo.PluginStatusRepository
import ch.chrigu.gmf.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
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
    suspend fun <PARENT : AggregateRoot<ID>, ID> update(
        entity: PARENT,
        pluginId: PluginStatusId,
        pluginData: FormValues,
        parentDefinition: ParentDefinition<PARENT, ID>
    ): PluginForm<*> {
        val plugin = resolvePluginDefinition(pluginId)
        val itemDefinition = parentDefinition.getItemDefinition(plugin) ?: throw AggregateNotFoundException("Item definition for plugin $pluginId not found")
        return pluginFormFactory.save(plugin.title, itemDefinition, pluginData, entity, parentDefinition.uriFor(entity, pluginId))
    }

    @PreAuthorize("hasRole('USER')")
    suspend fun <PARENT : AggregateRoot<ID>, ID> getForms(entity: PARENT, parentDefinition: ParentDefinition<PARENT, ID>): List<PluginForm<*>> {
        val activePlugins = pluginStatusRepository.findByActive(true).toList()
        val activePluginIds = activePlugins.map { it.metadata.pluginId }
        val resolvedPlugins = plugins.filter { activePluginIds.contains(it.id) }
        val forms = resolvedPlugins
            .mapNotNull { plugin ->
                val itemDefinition = parentDefinition.getItemDefinition(plugin) ?: return@mapNotNull null
                val activePlugin = activePlugins.first { it.metadata.pluginId == plugin.id }
                pluginFormFactory.create(plugin.title, itemDefinition, entity, parentDefinition.uriFor(entity, activePlugin.id))
            }
        return forms
    }

    private suspend fun resolvePluginDefinition(pluginId: PluginStatusId): Plugin {
        val status = pluginStatusRepository.findById(pluginId.toString()) ?: throw AggregateNotFoundException("Plugin $pluginId not found")
        require(status.active)
        return plugins.first { it.id == status.metadata.pluginId }
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
