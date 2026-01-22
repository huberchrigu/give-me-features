package ch.chrigu.gmf.plugins.web

import ch.chrigu.gmf.plugins.PluginService
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/plugins")
class PluginStatusController(private val pluginService: PluginService) {
    @GetMapping
    suspend fun getPlugins() = Rendering.view("plugins")
        .modelAttribute("plugins", pluginService.findAll().toList())
        .build()
}