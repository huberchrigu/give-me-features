package ch.chrigu.gmf.plugins

import ch.chrigu.gfm.plugin.ItemField
import ch.chrigu.gfm.plugin.ItemType

data class PluginForm<T>(val formFields: List<PluginFormField<*>>, val uri: String) {
    constructor(fields: List<ItemField<T>>, pluginData: T, uri: String) : this(fields.map { it.toFormField(pluginData) }, uri)
}

data class PluginFormField<T>(
    val id: String, val label: String, val type: String,
    val required: Boolean, val readOnly: Boolean, val value: T? = null
)

private fun ItemType.asInputType() = when (this) {
    ItemType.TEXT -> "text"
    ItemType.BOOLEAN -> "checkbox"
}

private fun <T> ItemField<T>.toFormField(pluginData: T) = PluginFormField(id, title, type.asInputType(), required, readOnly, get(pluginData))