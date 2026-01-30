package ch.chrigu.gmf.plugins.forms

import ch.chrigu.gfm.plugin.ItemDefinition
import ch.chrigu.gmf.plugins.PluginForm
import org.springframework.stereotype.Service

@Service
class PluginFormFactory {
    fun <PARENT, P> create(itemDefinition: ItemDefinition<PARENT, P>, pluginData: Map<String, Any?>, uri: String): PluginForm<P> {
        return PluginForm(itemDefinition.fields, itemDefinition.fromMap(pluginData), uri)
    }
}