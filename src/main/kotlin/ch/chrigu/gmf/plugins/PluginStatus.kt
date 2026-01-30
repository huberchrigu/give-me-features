package ch.chrigu.gmf.plugins

import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gfm.plugin.PluginId
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot
import java.util.UUID

class PluginStatus(val active: Boolean, val metadata: PluginMetadata, id: PluginStatusId, version: Long?) : AbstractAggregateRoot<PluginStatusId>(id, version) {
    fun activate(version: Long): PluginStatus {
        require(!active)
        return copy(version, active = true)
    }

    fun deactivate(version: Long): PluginStatus {
        require(active)
        return copy(version, active = false)
    }

    fun updateWith(plugin: Plugin) = PluginStatus(active, plugin.asMetadata(), id, version)

    private fun copy(version: Long, active: Boolean = this.active) = PluginStatus(active, metadata, id, version)

    companion object {
        fun from(plugin: Plugin) = PluginStatus(true, plugin.asMetadata(), PluginStatusId(), null)

        private fun Plugin.asMetadata() = PluginMetadata(id, title)
    }
}

data class PluginStatusId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString() = id
}

data class PluginMetadata(val pluginId: PluginId, val title: String)