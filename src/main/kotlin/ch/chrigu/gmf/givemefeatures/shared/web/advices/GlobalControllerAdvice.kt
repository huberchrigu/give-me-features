package ch.chrigu.gmf.givemefeatures.shared.web.advices

import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalControllerAdvice {
    @ModelAttribute("csrf")
    fun csrf(exchange: ServerWebExchange): Mono<CsrfToken>? {
        return exchange.getAttribute(CsrfToken::class.java.name)
    }
}
