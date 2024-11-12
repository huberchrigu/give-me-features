package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun renderHtml(e: Exception, exchange: ServerWebExchange): Rendering {
        val message = e.message ?: "Unknown error happened"
        val status = if (e is ResponseStatusException) e.statusCode else HttpStatus.INTERNAL_SERVER_ERROR
        val view = if (exchange.request.headers[Hx.HEADER_NAME]?.get(0) == "true") {
            @Suppress("SpringMVCViewInspection")
            Rendering.view("features :: error")
        } else
            Rendering.view("error")
        return view.modelAttribute("error", message)
            .status(status).build()
    }
}
