package ch.chrigu.gfm.plugin

data class Plugin(val id: PluginId, val title: String, val touchpoints: PluginTouchpoints)

data class PluginTouchpoints(val featureItem: ItemDefinition<FeatureReference, *>? = null, val taskItem: ItemDefinition<TaskReference, *>? = null)

data class ItemDefinition<PARENT, P>(val persistenceClass: Class<P>, val fields: List<ItemField<P>>, val triggers: ItemTriggers<PARENT>, val fromMap: Map<String, Any?>.() -> P)

data class ItemField<P>(val id: String, val type: ItemType, val title: String, val get: P.() -> Any, val required: Boolean = true, val readOnly: Boolean = false)

data class ItemTriggers<PARENT>(val onChange: (PARENT) -> PARENT = { it })
enum class ItemType { TEXT, BOOLEAN }

data class PluginId(private val id: String) {
    override fun toString() = id
}

interface FeatureReference
interface TaskReference