package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import kotlinx.coroutines.flow.Flow

/**
 * E.g., a task or a feature, an aggregate which can be extended by plugins.
 */
interface ParentDefinition<PARENT : AggregateRoot<ID>, ID> {
    val uriPrefix: String
    val changes: Flow<PARENT>
    fun getItemDefinition(plugin: Plugin): ItemDefinition<PARENT, ID, *>?
    suspend fun resolve(id: String): PARENT
    fun uriFor(entity: PARENT, pluginId: PluginStatusId) = "$uriPrefix/${entity.id}/plugins/$pluginId"
}