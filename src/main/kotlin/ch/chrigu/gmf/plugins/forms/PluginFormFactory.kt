package ch.chrigu.gmf.plugins.forms

import ch.chrigu.gmf.plugins.FormValues
import ch.chrigu.gmf.plugins.ItemDefinition
import ch.chrigu.gmf.plugins.PluginForm
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import org.springframework.stereotype.Service
import kotlin.collections.emptyMap

@Service
class PluginFormFactory {
    suspend fun <PARENT, ID, P : Any> save(
        title: String,
        itemDefinition: ItemDefinition<PARENT, ID, P>,
        pluginData: FormValues,
        entity: PARENT,
        uri: String
    ): PluginForm<P> {
        val savedData = itemDefinition.repository.save(itemDefinition.fromMap(pluginData, entity))
        return create(title, itemDefinition, savedData, uri)
    }

    suspend fun <PARENT : AggregateRoot<ID>, ID, P : Any> create(title: String, itemDefinition: ItemDefinition<PARENT, ID, P>, entity: PARENT, uri: String): PluginForm<P> {
        val pluginData = itemDefinition.repository.findById(entity.id!!) ?: itemDefinition.fromMap(emptyMap(), entity)
        return create(title, itemDefinition, pluginData, uri)
    }

    private fun <PARENT, ID, P : Any> create(title: String, itemDefinition: ItemDefinition<PARENT, ID, P>, pluginData: P, uri: String): PluginForm<P> {
        return PluginForm(title, itemDefinition.fields, pluginData, uri)
    }
}