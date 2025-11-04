package ch.chrigu.gmf.givemefeatures.shared.web.advices

import ch.chrigu.gmf.givemefeatures.shared.web.Hx
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.shared.history.VersionNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
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
    fun handleVersionNotFound(e: VersionNotFoundException, exchange: ServerWebExchange) = renderError(e, exchange, HttpStatus.BAD_REQUEST)

    @ExceptionHandler
    fun handleResponseStatusException(e: ResponseStatusException, exchange: ServerWebExchange) = renderError(e, exchange, e.statusCode)

    @ExceptionHandler
    fun handleOptimisticLockingException(e: OptimisticLockingFailureException, exchange: ServerWebExchange) =
        renderError(e, exchange, HttpStatus.BAD_REQUEST, "Unfortunately, there were concurrent modifications. Please add your changes again.")

    @ExceptionHandler
    fun handleFallback(e: Exception, exchange: ServerWebExchange) = renderError(e, exchange, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun renderError(e: Exception, exchange: ServerWebExchange, status: HttpStatusCode, message: String = e.message ?: "Unknown error happened"): Rendering {
        logger.error("Request ${exchange.request.uri} lead to error", e)
        val view = if (exchange.request.headers[Hx.HEADER_NAME]?.get(0) == "true") {
            @Suppress("SpringMVCViewInspection")
            Rendering.view("blocks/error")
        } else
            Rendering.view("error")
        return view.modelAttribute("error", message)
            .status(status).build()
    }
}
