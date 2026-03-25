package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.shared.aggregates.AggregateRoot

data class Plugin(val id: PluginId, val title: String, val touchpoints: PluginTouchpoints)

data class PluginTouchpoints(
    val featureItem: ItemDefinition<FeatureReference, FeatureReferenceId, *>? = null,
    val taskItem: ItemDefinition<TaskReference, TaskReferenceId, *>? = null
)

data class ItemDefinition<PARENT, ID, P : Any>(
    val persistenceClass: Class<P>,
    val fields: List<ItemField<P>>,
    val triggers: ItemTriggers<PARENT>,
    val repository: PluginRepository<P, ID>,
    val fromMap: Map<String, List<String>>.(PARENT) -> P
)

data class ItemField<P>(val id: String, val type: ItemType, val title: String, val get: P.() -> Any, val required: Boolean = true, val readOnly: Boolean = false)

data class ItemTriggers<PARENT>(val onChange: OnChangeTrigger<PARENT> = { })
typealias OnChangeTrigger<PARENT> = suspend (PARENT) -> Unit

enum class ItemType { TEXT, BOOLEAN }

data class PluginId(private val id: String) {
    override fun toString() = id
}

interface FeatureReference : AggregateRoot<FeatureReferenceId>
interface FeatureReferenceId
interface TaskReference : AggregateRoot<TaskReferenceId>
interface TaskReferenceId
