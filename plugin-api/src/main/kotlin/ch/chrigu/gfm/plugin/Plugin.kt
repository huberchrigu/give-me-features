package ch.chrigu.gfm.plugin

data class Plugin(val id: PluginId, val title: String, val touchpoints: PluginTouchpoints)

data class PluginTouchpoints(val featureItem: ItemDefinition<Feature>? = null, val taskItem: ItemDefinition<Task>? = null)

data class ItemDefinition<T>(val fields: List<ItemField>, val triggers: ItemTriggers<T>)

data class ItemField(val type: ItemType, val title: String, val required: Boolean = true, val readOnly: Boolean = false)

data class ItemTriggers<T>(val onChange: (T) -> T = { it })
enum class ItemType { TEXT, BOOLEAN }

data class PluginId(private val id: String) {
    override fun toString() = id
}

interface Feature
interface Task