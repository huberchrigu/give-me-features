package ch.chrigu.gmf.features.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class RootController {
    @GetMapping("/")
    fun redirectToFeatures() = Rendering.redirectTo("/features").build()
}
