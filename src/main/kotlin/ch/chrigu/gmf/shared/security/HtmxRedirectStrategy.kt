package ch.chrigu.gmf.shared.security

import ch.chrigu.gmf.shared.security.SecurityConfiguration.Companion.LOGIN_PAGE
import ch.chrigu.gmf.shared.web.Hx
import org.springframework.http.HttpStatus
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

class HtmxRedirectStrategy : DefaultServerRedirectStrategy() {
    override fun sendRedirect(exchange: ServerWebExchange, location: URI): Mono<Void> {
        return Mono.fromRunnable {
            exchange.response.apply {
                headers.location = URI.create(LOGIN_PAGE)
                if (exchange.request.headers.containsHeader(Hx.REQUEST)) {
                    headers.add(Hx.REDIRECT, LOGIN_PAGE)
                } else {
                    statusCode = HttpStatus.FOUND
                }
            }
        }
    }
}
