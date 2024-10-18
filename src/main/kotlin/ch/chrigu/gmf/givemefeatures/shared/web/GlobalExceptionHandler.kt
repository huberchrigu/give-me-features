package ch.chrigu.gmf.givemefeatures.shared.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.result.view.Rendering

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun renderHtml(e: Exception) = Rendering.view("features :: error").modelAttribute("error", e.message ?: "Unknown error happened").build()
}