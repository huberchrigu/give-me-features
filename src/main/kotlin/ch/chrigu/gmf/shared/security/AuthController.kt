package ch.chrigu.gmf.shared.security

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class AuthController {
    @GetMapping(SecurityConfiguration.LOGIN_PAGE)
    fun loginPage(): Rendering = Rendering.view("login").build()
}