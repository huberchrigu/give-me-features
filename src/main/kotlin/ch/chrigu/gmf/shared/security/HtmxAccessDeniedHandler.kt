package ch.chrigu.gmf.shared.security

import ch.chrigu.gmf.shared.security.SecurityConfiguration.Companion.LOGIN_PAGE
import ch.chrigu.gmf.shared.web.Hx
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Deprecated("Maybe this is not even needed")
class HtmxAccessDeniedHandler : ServerAccessDeniedHandler {
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> =
        exchange.response.run {
            if (exchange.request.headers.containsHeader(Hx.REQUEST)) {
                headers.add(Hx.REDIRECT, LOGIN_PAGE)
                statusCode = HttpStatus.OK
            } else {
                statusCode = HttpStatus.FORBIDDEN
            }
            setComplete()
        }
}
