package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.features.copy
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.web.UiTest
import ch.chrigu.gmf.givemefeatures.tasks.*
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
    private val featureDescription = Html("<p>Description</p>")
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

    @Test
    fun `should open feature directly`() {
        withFeature()

        openFeaturesPage(featureId) {
            assertFeatureList(true)
            assertFeatureDetails()
        }
    }

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
        val expectedFeatureWithId = expectedFeature.copy(id = featureId, version = 0)
        every { featureService.getFeatures() }.returnsMany(emptyFlow(), flowOf(expectedFeatureWithId))
        coEvery { featureService.newFeature(match { expectedFeature.copy(id = it.id) == it }) } returns expectedFeatureWithId
        every { taskService.resolve(emptyList()) } returns emptyFlow()
    }

    private fun withTask(feature: Feature) {
        val task = Task.describeNewTask(taskName)
        val featureWithTask = feature.copy(tasks = listOf(taskId))
        coEvery { featureService.addTask(featureId, 0L, match { it == Task(it.id, null, taskName, Html(""), TaskStatus.OPEN) }) } returns featureWithTask
        every { taskService.resolve(listOf(taskId)) } returns flowOf(task.copy(id = taskId))
    }

    private fun withFeature(): Feature {
        val feature = Feature(featureId, featureName, featureDescription, emptyList(), 0)
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
        waitForCondition { querySelector("#feature ul li") != null }
    }

    private fun Page.assertTask() {
        val taskElement = querySelector("#feature ul li a")
        assertThat(taskElement.textContent()).isEqualTo("$taskName OPEN")
    }

    private fun openFeaturesPage(selectFeature: FeatureId? = null, test: Page.() -> Unit) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            val append = selectFeature?.let { "/$selectFeature" } ?: ""
            page.navigate("http://localhost:$port/features$append")

            test(page)

            browser.close()
        }
    }

    private fun Page.assertFeatureDetails() {
        val feature = querySelector("#feature")
        assertThat(feature.querySelector("h2").textContent()).isEqualTo(featureName)
        assertThat(feature.querySelector("p").textContent()).isEqualTo("Description")
    }

    private fun Page.assertFeatureList(current: Boolean) {
        val items = querySelectorAll("#features li")
        assertThat(items).hasSize(1)
        assertThat(items[0].querySelector("span").textContent()).isEqualTo(featureName)
        val link = items[0].querySelector("a")
        assertThat(link.getAttribute("hx-get")).isEqualTo("/features/$featureId")
        val clazz = link.getAttribute("class").split(" ")
        if (current) {
            assertThat(clazz).contains("current")
        } else {
            assertThat(clazz).doesNotContain("current")
        }
    }

    private fun Page.assertError(prefix: String) {
        val error = querySelector("#error p")
        assertThat(error.textContent()).startsWith(prefix)
    }

    private fun Page.submitNewFeatureForm(name: String = featureName) {
        querySelector("#name").fill(name)
        frames()[1].querySelector("body#tinymce").fill("Description")
        locator("button[type='submit']").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }
}
