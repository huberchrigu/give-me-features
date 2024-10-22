package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ServerWebExchange

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun renderHtml(e: Exception, exchange: ServerWebExchange): Rendering {
        val message = e.message ?: "Unknown error happened"
        return if (exchange.request.headers[Hx.HEADER_NAME]?.get(0) == "true") {
            Rendering.view("features :: error").modelAttribute("error", message).build()
        } else
            Rendering.view("error").modelAttribute("error", message).build()
    }
}