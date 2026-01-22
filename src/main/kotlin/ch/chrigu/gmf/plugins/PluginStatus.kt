package ch.chrigu.gmf.plugins

import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gfm.plugin.PluginId
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot
import java.util.UUID

class PluginStatus(val active: Boolean, val pluginId: PluginId, id: PluginStatusId, version: Long?) : AbstractAggregateRoot<PluginStatusId>(id, version) {
    fun activate(): PluginStatus {
        require(!active)
        return copy(active = true)
    }

    fun deactivate(): PluginStatus {
        require(active)
        return copy(active = false)
    }

    private fun copy(active: Boolean = this.active) = PluginStatus(active, pluginId, id, version)

    companion object {
        fun from(plugin: Plugin) = PluginStatus(true, plugin.id, PluginStatusId(), null)
    }
}

data class PluginStatusId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString() = id
}