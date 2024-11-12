package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.web.UiTest
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
import org.springframework.boot.test.web.server.LocalServerPort

@UiTest(FeatureController::class)
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

        openFeaturesPage {
            submitNewFeatureForm()
            assertFeatureList(true)
            assertFeatureDetails()
        }
    }

    @Test
    fun `should throw error`() {
        coEvery { featureService.getFeatures() } returns emptyFlow()

        openFeaturesPage {
            submitNewFeatureForm("")
            assertError("Validation failed")
        }
    }

    @Test
    fun `should select a feature`() {
        withFeature()

        openFeaturesPage {
            assertFeatureList(false)
            clickOnFeatureListItem()
            assertFeatureDetails()
        }
    }

    /**
     * Fails because mocked invocation
     * `FeatureService(ch.chrigu.gmf.givemefeatures.features.FeatureService#0 bean#1).addTask-WFusim8(123, Task(id=null, name=New task, description=, status=OPEN), continuation {})`
     * does not work.
     */
    @Test
    fun `should add a task`() {
        val feature = withFeature()
        withTask(feature)
        openFeaturesPage {
            assertFeatureList(false)
            clickOnFeatureListItem()
            assertFeatureDetails()
            submitNewTaskForm()
            assertTask()
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

    private fun Page.clickOnFeatureListItem() {
        querySelector("#features li a").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.submitNewTaskForm() {
        val form = querySelector("#feature form")
        form.querySelector("#taskName").fill(taskName)
        form.querySelector("button[type='submit']").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.assertTask() {
        val taskElement = querySelector("#feature ul li")
        assertThat(taskElement.innerHTML()).isEqualTo(taskName)
    }

    private fun openFeaturesPage(test: Page.() -> Unit) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            page.navigate("http://localhost:$port/features")

            test(page)

            browser.close()
        }
    }

    private fun Page.assertFeatureDetails() {
        val feature = querySelector("#feature")
        assertThat(feature.querySelector("h2").textContent()).isEqualTo(featureName)
        assertThat(feature.querySelector("p").textContent()).isEqualTo(featureDescription)
    }

    private fun Page.assertFeatureList(current: Boolean) {
        val items = querySelectorAll("#features li")
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

    private fun Page.assertError(prefix: String) {
        val error = querySelector("#error p")
        assertThat(error.textContent()).startsWith(prefix)
    }

    private fun Page.submitNewFeatureForm(name: String = featureName) {
        querySelector("#name").fill(name)
        querySelector("#description").fill(featureDescription)
        locator("button[type='submit']").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

}
