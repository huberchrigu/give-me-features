package ch.chrigu.gmf.plugins.web

import ch.chrigu.gfm.plugin.ItemDefinition
import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gmf.plugins.PluginService
import ch.chrigu.gmf.plugins.PluginStatusId
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.reactive.result.view.Rendering

interface PluginFormController<PARENT, ID> {
    val pluginService: PluginService

    @PutMapping("{id}/plugins/{pluginId}")
    suspend fun <P> updatePluginData(@PathVariable id: ID, @PathVariable pluginId: PluginStatusId, @RequestBody body: Map<String, Any?>): Rendering =
        Rendering.view("plugins/plugin-form")
            .modelAttribute("plugin", pluginService.update(resolve(id), pluginId, body, this::getItemDefinition))
            .build()

    suspend fun getPluginsFor(id: ID) = pluginService.getForms(resolve(id), this::getItemDefinition)

    suspend fun resolve(id: ID): PARENT

    fun getItemDefinition(plugin: Plugin): ItemDefinition<PARENT, *>?
}