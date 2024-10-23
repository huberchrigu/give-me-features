package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

@SpringBootTest(classes = [FeatureController::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(FeatureControllerUiTest.WebFluxTestConfig::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class FeatureControllerUiTest(@MockkBean private val featureService: FeatureService, @MockkBean private val taskService: TaskService) {
    private val featureName = "My new feature"
    private val featureDescription = "Description"
    private val featureId = FeatureId("123")
    private val taskName = "New task"
    private val taskId = TaskId("99")

    @LocalServerPort
    private var port: Int = 0

    @Test
    fun `should create a new feature`() {
        withNewFeature()

        openFeaturesPage { page ->
            submitNewFeatureForm(page)
            assertFeatureList(page, true)
            assertFeatureDetails(page)
        }
    }

    @Test
    fun `should select a feature`() {
        withFeature()

        openFeaturesPage { page ->
            assertFeatureList(page, false)
            clickOnFeatureListItem(page)
            assertFeatureDetails(page)
        }
    }

    @Test
    fun `should add a task`() {
        val feature = withFeature()
        withTask(feature)
        openFeaturesPage { page ->
            assertFeatureList(page, false)
            clickOnFeatureListItem(page)
            assertFeatureDetails(page)
            submitNewTaskForm(page)
            assertTask(page)
        }
    }

    private fun withNewFeature() {
        val expectedFeature = Feature.describeNewFeature(featureName, featureDescription)
        val expectedFeatureWithId = expectedFeature.copy(id = featureId)
        every { featureService.getFeatures() }.returnsMany(emptyFlow(), flowOf(expectedFeatureWithId))
        coEvery { featureService.newFeature(expectedFeature) } returns expectedFeatureWithId
        every { taskService.resolve(emptyList()) } returns emptyFlow()
    }

    private fun withTask(feature: Feature) {
        val task = Task.describeNewTask(taskName)
        val featureWithTask = feature.copy(tasks = listOf(taskId))
        coEvery { featureService.addTask(featureId, task) } returns featureWithTask
        every { taskService.resolve(listOf(taskId)) } returns flowOf(task.copy(id = taskId))
    }

    private fun withFeature(): Feature {
        val feature = Feature(featureId, featureName, featureDescription, emptyList())
        every { featureService.getFeatures() } returns flowOf(feature)
        coEvery { featureService.getFeature(featureId) } returns feature
        every { taskService.resolve(emptyList()) } returns emptyFlow()
        return feature
    }

    private fun clickOnFeatureListItem(page: Page) {
        page.querySelector("#features li a").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun submitNewTaskForm(page: Page) {
        val form = page.querySelector("#feature form")
        form.querySelector("#taskName").fill(taskName)
        form.querySelector("button[type='submit']").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun assertTask(page: Page) {
        val taskElement = page.querySelector("#feature ul li")
        assertThat(taskElement.innerHTML()).isEqualTo(taskName)
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

    private fun assertFeatureDetails(page: Page) {
        val feature = page.querySelector("#feature")
        assertThat(feature.querySelector("h2").textContent()).isEqualTo(featureName)
        assertThat(feature.querySelector("p").textContent()).isEqualTo(featureDescription)
    }

    private fun assertFeatureList(page: Page, current: Boolean) {
        val items = page.querySelectorAll("#features li")
        assertThat(items).hasSize(1)
        assertThat(items[0].querySelector("span").textContent()).isEqualTo(featureName)
        val link = items[0].querySelector("a")
        assertThat(link.getAttribute("hx-get")).isEqualTo("/features/$featureId")
        val clazz = link.getAttribute("class")
        if (current) {
            assertThat(clazz).isEqualTo("current")
        } else {
            assertThat(clazz).isNull()
        }
    }

    private fun submitNewFeatureForm(page: Page) {
        page.querySelector("#name").fill(featureName)
        page.querySelector("#description").fill(featureDescription)
        page.locator("button[type='submit']").click()
        page.waitForLoadState(LoadState.NETWORKIDLE)
    }

    @TestConfiguration
    @EnableAutoConfiguration(exclude = [MongoReactiveAutoConfiguration::class])
    @Import(SecurityConfiguration::class)
    class WebFluxTestConfig
}
