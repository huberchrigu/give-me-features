package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.shared.aggregates.AggregateRoot

/**
 * E.g., a task or a feature, an aggregate which can be extended by plugins.
 */
interface ParentDefinition<PARENT : AggregateRoot<ID>, ID> {
    val uriPrefix: String
    fun getItemDefinition(plugin: Plugin): ItemDefinition<PARENT, ID, *>?
    suspend fun resolve(id: String): PARENT
    fun uriFor(entity: PARENT, pluginId: PluginStatusId) = "$uriPrefix/${entity.id}/plugins/$pluginId"
}