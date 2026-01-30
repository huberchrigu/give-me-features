package ch.chrigu.gmf.plugins.web

import ch.chrigu.gmf.plugins.PluginService
import ch.chrigu.gmf.plugins.PluginStatus
import ch.chrigu.gmf.plugins.PluginStatusId
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException

@Controller
@RequestMapping("/plugins")
class PluginStatusController(private val pluginService: PluginService) {
    @GetMapping
    suspend fun getPlugins(): Rendering = Rendering.view("plugins")
        .modelAttribute("plugins", pluginService.findAll().toList())
        .build()

    @PostMapping("/{id}/activate")
    suspend fun activate(@PathVariable id: PluginStatusId, @RequestParam version: Long): Rendering = renderPlugin(pluginService.activatePlugin(id, version))

    @PostMapping("/{id}/deactivate")
    suspend fun deactivate(@PathVariable id: PluginStatusId, @RequestParam version: Long): Rendering = renderPlugin(pluginService.deactivatePlugin(id, version))

    private suspend fun renderPlugin(plugin: PluginStatus?) = if (plugin == null)
        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    else
        Rendering.view("plugins/plugin")
            .modelAttribute("plugin", plugin)
            .build()
}