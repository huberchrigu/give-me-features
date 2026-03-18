package ch.chrigu.gmf.plugins.web

import ch.chrigu.gmf.plugins.ParentDefinition
import ch.chrigu.gmf.plugins.PluginService
import ch.chrigu.gmf.plugins.PluginStatusId
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException

@Controller
class PluginFormController(private val pluginService: PluginService, private val parentDefinitions: List<ParentDefinition<*, *>>) {

    @PutMapping("/{parent}/{id}/plugins/{pluginId}")
    suspend fun <PARENT : AggregateRoot<ID>, ID> updatePluginData(
        @PathVariable parent: String,
        @PathVariable id: String,
        @PathVariable pluginId: PluginStatusId,
        body: Map<String, Any?>
    ): Rendering {
        val parentDefinition = find(parent) as ParentDefinition<PARENT, ID>? ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return Rendering.view("plugins/plugin-form")
            .modelAttribute("plugin", pluginService.update(parentDefinition.resolve(id), pluginId, body, parentDefinition))
            .build()
    }

    private fun find(uriPrefix: String) = parentDefinitions.firstOrNull { it.uriPrefix == "/$uriPrefix" }
}