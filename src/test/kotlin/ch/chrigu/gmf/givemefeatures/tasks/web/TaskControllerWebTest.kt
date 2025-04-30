package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import gg.jte.springframework.boot.autoconfigure.ReactiveJteAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@WebFluxTest(controllers = [TaskController::class])
@AutoConfigureWebTestClient(timeout = "30s")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(SecurityConfiguration::class, ReactiveJteAutoConfiguration::class)
class TaskControllerWebTest(
    private val webTestClient: WebTestClient,
    @MockitoBean private val taskService: TaskService
) {
    @Test
    fun `should return client-friendly 400 error`() {
        webTestClient.mutateWith(csrf())
            .put().uri("/tasks/123/status?version=0")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
            .header(Hx.HEADER_NAME, "true")
            .body(BodyInserters.empty<String>())
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith { response ->
                assertThat(String(response.responseBody!!)).contains("must not be null")
            }
    }
}
