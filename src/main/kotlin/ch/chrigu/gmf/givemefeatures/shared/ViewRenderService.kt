package ch.chrigu.gmf.givemefeatures.shared

import org.springframework.web.server.ServerWebExchange

interface ViewRenderService {
    suspend fun render(viewName: String, params: Map<String, Any?> = emptyMap(), exchange: ServerWebExchange): String
}
