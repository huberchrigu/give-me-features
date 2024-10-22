package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import ch.chrigu.gmf.givemefeatures.test.TestProperties
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import kotlinx.coroutines.flow.asFlow
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
    private val taskId = TaskId("xxx")
    private val features = mutableListOf<Feature>()
    private val tasks = mutableListOf<Task>()

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun mockServices() {
        features.clear()
        tasks.clear()
        runBlocking {
            whenever(featureService.newFeature(any())) doAnswer {
                val feature = it.arguments[0] as Feature
                feature.copy(id = id).also { f -> features.add(f) }
            }
            whenever(taskService.newTask(any())) doAnswer {
                val task = it.arguments[0] as Task
                task.copy(id = taskId).also { t -> tasks.add(t) }
            }
            whenever(featureService.getFeatures()) doAnswer { features.asFlow() }
            whenever(taskService.resolve(any())) doReturn tasks.asFlow()
        }
    }

    @Test
    fun `should create a new feature`() {
        openFeaturesPage { page ->
            submitNewFeatureForm(page, "My new feature", "Description")
            assertFeatureList(page, "My new feature", true)
            assertFeatureDetails(page, "My new feature", "Description")
        }
    }

    @Test
    fun `should select a feature`() {
        withFeature("a", "b")
        openFeaturesPage { page ->
            assertFeatureList(page, "a", false)
            clickOnFeatureListItem(page)
            assertFeatureDetails(page, "a", "b")
        }
    }

    @Test
    fun `should add a task`() {
        withFeature("a", "b")
        openFeaturesPage { page ->
            assertFeatureList(page, "a", false)
            clickOnFeatureListItem(page)
            assertFeatureDetails(page, "a", "b")
            submitNewTaskForm(page, "New task")
            assertTask(page, "New task")
        }
    }

    private fun withFeature(name: String, description: String) {
        features.add(Feature(id, name, description, emptyList()))
    }

    private fun clickOnFeatureListItem(page: Page) {
        page.querySelector("#features li a").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun submitNewTaskForm(page: Page, name: String) {
        val form = page.querySelector("#feature form")
        form.querySelector("#taskName").fill(name)
        form.querySelector("button[type='submit']").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun assertTask(page: Page, name: String) {
        val taskElement = page.querySelector("#feature li")
        assertThat(taskElement.innerHTML()).isEqualTo(name)
    }

    private fun openFeaturesPage(test: (Page) -> Unit) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            page.navigate("http://localhost:$port/features")

            test(page)

            browser.close()
        }
    }

    private fun assertFeatureDetails(page: Page, name: String, description: String) {
        val feature = page.querySelector("#feature")
        assertThat(feature.querySelector("h2").textContent()).isEqualTo(name)
        assertThat(feature.querySelector("p").textContent()).isEqualTo(description)
    }

    private fun assertFeatureList(page: Page, name: String, current: Boolean) {
        val items = page.querySelectorAll("#features li")
        assertThat(items).hasSize(1)
        assertThat(items[0].querySelector("span").textContent()).isEqualTo(name)
        val link = items[0].querySelector("a")
        assertThat(link.getAttribute("hx-get")).isEqualTo("/features/$id")
        val clazz = link.getAttribute("class")
        if (current) {
            assertThat(clazz).isEqualTo("current")
        } else {
            assertThat(clazz).isNull()
        }
    }

    private fun submitNewFeatureForm(page: Page, name: String, description: String) {
        page.querySelector("#name").fill(name)
        page.querySelector("#description").fill(description)
        page.locator("button[type='submit']").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    @TestConfiguration
    @EnableAutoConfiguration(exclude = [MongoReactiveAutoConfiguration::class])
    @Import(SecurityConfiguration::class)
    class WebFluxTestConfig
}