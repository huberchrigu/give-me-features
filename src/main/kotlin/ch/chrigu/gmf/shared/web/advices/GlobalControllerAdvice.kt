package ch.chrigu.gmf.shared.web.advices

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalControllerAdvice {
    @ModelAttribute("global")
    suspend fun csrf(exchange: ServerWebExchange) = GlobalModel(
        exchange.getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.name)!!.awaitFirst(),
        exchange.getPrincipal<UsernamePasswordAuthenticationToken>().awaitFirstOrNull()
    )
}

class GlobalModel(val csrf: CsrfToken, val auth: UsernamePasswordAuthenticationToken?) {
    fun hasRole(role: String) = auth?.authorities?.any { it.authority == "ROLE_$role" } == true
}
