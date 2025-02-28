package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.view.Rendering
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    fun handleNotFound(e: AggregateNotFoundException, exchange: ServerWebExchange) = renderError(e, exchange, HttpStatus.NOT_FOUND)

    @ExceptionHandler
    fun handleResponseStatusException(e: ResponseStatusException, exchange: ServerWebExchange) = renderError(e, exchange, e.statusCode)

    @ExceptionHandler
    fun handleFallback(e: Exception, exchange: ServerWebExchange) = renderError(e, exchange, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun renderError(e: Exception, exchange: ServerWebExchange, status: HttpStatusCode): Rendering {
        logger.error("Request ${exchange.request.uri} lead to error", e)
        val message = e.message ?: "Unknown error happened"
        val view = if (exchange.request.headers[Hx.HEADER_NAME]?.get(0) == "true") {
            @Suppress("SpringMVCViewInspection")
            Rendering.view("blocks/error")
        } else
            Rendering.view("error")
        return view.modelAttribute("error", message)
            .status(status).build()
    }
}
