package ch.chrigu.gmf.plugins

import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gmf.plugins.mongo.PluginStatusRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PluginService(private val pluginStatusRepository: PluginStatusRepository, private val plugins: List<Plugin> = emptyList()) {
    @PostConstruct
    fun init() {
        runBlocking {
            val current = pluginStatusRepository.findAll().toList()
            val new = plugins minusPluginStatus current
            val removed = current minusPlugins plugins
            pluginStatusRepository.saveAll(new.map { PluginStatus.from(it) }).toList()
            pluginStatusRepository.deleteAll(removed)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    suspend fun activatePlugin(id: PluginStatusId) = doWith(id) { activate() }

    @PreAuthorize("hasRole('ADMIN')")
    suspend fun deactivatePlugin(id: PluginStatusId) = doWith(id) { deactivate() }

    @PreAuthorize("hasRole('ADMIN')")
    fun findAll(): Flow<PluginStatus> = pluginStatusRepository.findAll()

    private suspend fun doWith(id: PluginStatusId, apply: PluginStatus.() -> PluginStatus) = pluginStatusRepository.findById(id.toString())?.apply()?.let {
        pluginStatusRepository.save(it)
    }

    private infix fun List<Plugin>.minusPluginStatus(other: List<PluginStatus>): List<Plugin> {
        val otherIds = other.map { it.pluginId }
        return filter { !otherIds.contains(it.id) }
    }

    private infix fun List<PluginStatus>.minusPlugins(other: List<Plugin>): List<PluginStatus> {
        val otherIds = other.map { it.id }
        return filter { !otherIds.contains(it.pluginId) }
    }
}
