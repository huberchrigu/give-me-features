package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.shared.web.GlobalControllerAdvice
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import gg.jte.springframework.boot.autoconfigure.JteProperties
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange

@Service
class HtmlRenderService(private val templateEngine: TemplateEngine, private val jteProperties: JteProperties, private val globalControllerAdvice: GlobalControllerAdvice) {
    suspend fun render(viewName: String, params: Map<String, Any?> = emptyMap(), exchange: ServerWebExchange): String {
        val output = StringOutput()
        val withCsrf = params + mapOf("csrf" to globalControllerAdvice.csrf(exchange)?.awaitSingle())
        templateEngine.render("${viewName}${jteProperties.templateSuffix}", withCsrf, output)
        return output.toString()
    }
}
