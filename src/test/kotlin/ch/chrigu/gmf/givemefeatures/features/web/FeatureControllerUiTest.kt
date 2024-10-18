package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import ch.chrigu.gmf.givemefeatures.test.TestProperties
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

@SpringBootTest(classes = [FeatureController::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [TestProperties.DEBUG_AUTO_CONFIGURATION])
@Import(FeatureControllerUiTest.WebFluxTestConfig::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class FeatureControllerUiTest(@MockBean private val featureService: FeatureService, @MockBean private val taskService: TaskService) {
    private val id = FeatureId("123")
    private val features = mutableListOf<Feature>()

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun initService() {
        runBlocking {
            whenever(featureService.newFeature(any())) doAnswer {
                val feature = it.arguments[0] as Feature
                feature.copy(id = id).also { f -> features.add(f) }
            }
            whenever(featureService.getFeatures()) doAnswer { features.asFlow() }
            whenever(taskService.resolve(any())) doReturn emptyFlow()
        }
    }

    @Test
    fun `should create a new feature`() {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            page.navigate("http://localhost:$port/features")

            page.querySelector("#name").fill("My new feature")
            page.querySelector("#description").fill("Description")
            page.locator("button[type='submit']").click()

            page.waitForLoadState(LoadState.NETWORKIDLE)
            val items = page.querySelectorAll("#features li")
            assertThat(items).hasSize(1)
            assertThat(items[0].querySelector("span").textContent()).isEqualTo("My new feature")
            assertThat(items[0].querySelector("a").getAttribute("hx-get")).isEqualTo("/features/$id")

            val feature = page.querySelector("#feature")
            assertThat(feature.querySelector("h2").textContent()).isEqualTo("My new feature")
            assertThat(feature.querySelector("p").textContent()).isEqualTo("Description")

            browser.close()
        }
    }

    @Test
    fun `should select a feature`() {
        TODO()
    }

    @Test
    fun `should add a task`() {
        TODO()
    }

    @TestConfiguration
    @EnableAutoConfiguration(exclude = [MongoReactiveAutoConfiguration::class])
    @Import(SecurityConfiguration::class)
    class WebFluxTestConfig
}