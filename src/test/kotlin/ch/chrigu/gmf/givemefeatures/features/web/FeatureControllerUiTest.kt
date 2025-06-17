package ch.chrigu.gmf.givemefeatures.features.web

import ch.chrigu.gmf.givemefeatures.features.*
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.shared.web.UiTest
import ch.chrigu.gmf.givemefeatures.tasks.*
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@UiTest(FeatureController::class)
class FeatureControllerUiTest(@MockkBean private val featureService: FeatureService, @MockkBean private val taskService: TaskService) {
    private val featureName = "My new feature"
    private val newName = "Version 2.0"
    private val featureDescription = Markdown("Description")
    private val newDescription = "New description"
    private val featureId = FeatureId("123")
    private val taskName = "New task"
    private val taskId = TaskId("99")

    private val changes = MutableSharedFlow<Feature>()

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun initChanges() {
        every { featureService.getUpdates(any()) } answers { changes.filter { it.id == firstArg<FeatureId>() } }
        every { featureService.getAllUpdates() } returns changes.asSharedFlow()
    }

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
    fun `should open and update feature`() {
        withFeature()
        withFeatureUpdate()

        openFeaturesPage(featureId) {
            assertFeatureDetails()
            editFeature()
            assertFeatureDetails(newName, newDescription)
        }
    }

    @Test
    fun `should show changes in feature list immediately`() {
        withFeature()

        openFeaturesPage {
            assertFeatureList(false)
            clickOnFeatureListItem()
            assertFeatureDetails()
            withExternalChange()
            assertFeatureDetails(newName, newDescription)
            assertFeatureList(true)
        }
    }

    @Test
    fun `should show changes in feature details immediately`() {
        withFeature()

        openFeaturesPage(featureId) {
            assertFeatureDetails()
            withExternalChange()
            assertFeatureDetails(newName, newDescription)
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
        coEvery { featureService.addTask(featureId, 0L, match { it == Task(it.id, null, taskName, Markdown(""), TaskStatus.OPEN) }) } returns featureWithTask
        every { taskService.resolve(listOf(taskId)) } returns flowOf(task.copy(id = taskId))
    }

    private fun withFeature(): Feature {
        val feature = Feature(featureId, featureName, featureDescription, emptyList(), 0)
        every { featureService.getFeatures() } returns flowOf(feature)
        coEvery { featureService.getFeature(featureId) } returns feature
        every { taskService.resolve(emptyList()) } returns emptyFlow()
        return feature
    }

    private fun withFeatureUpdate() {
        val markdown = Markdown(newDescription)
        coEvery { featureService.updateFeature(featureId, 0L, FeatureUpdate(newName, markdown)) } returns Feature(featureId, newName, markdown, emptyList(), 1L)
    }

    private fun Page.withExternalChange() = runBlocking {
        changes.emit(Feature(featureId, newName, Markdown(newDescription), emptyList(), 1L))
        waitForCondition { querySelector("#feature h2").textContent() != featureName }
    }

    private fun Page.clickOnFeatureListItem() {
        querySelector("#features li a").click()
        waitForCondition { querySelector("#feature h2") != null }
    }

    private fun Page.submitNewTaskForm() {
        val form = querySelector("#feature form")
        form.querySelector("#taskName").fill(taskName)
        form.querySelector("button[type='submit']").click()
        waitForCondition { querySelector("#feature ul li") != null }
    }

    private fun Page.editFeature() {
        val editButton = querySelectorAll("button").first { it.textContent() == "Edit" }
        editButton.click()
        waitForLoadState(LoadState.NETWORKIDLE)
        val nameField = querySelector("input#name")
        val descriptionField = querySelector("#description")
        val submitButton = querySelector("button[hx-patch]")
        nameField.fill(newName)
        descriptionField.fill(newDescription)
        submitButton.click()
        waitForLoadState(LoadState.NETWORKIDLE)
        waitForCondition { querySelector("button[hx-patch]") == null }
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

    private fun Page.assertFeatureDetails(expectedName: String = featureName, expectedDescription: String = "Description") {
        val feature = querySelector("#feature")
        assertThat(feature.querySelector("h2").textContent()).isEqualTo(expectedName)
        assertThat(feature.querySelector("p").textContent()).isEqualTo(expectedDescription)
    }

    private fun Page.assertFeatureList(current: Boolean) {
        val items = querySelectorAll("#features li")
        assertThat(items).hasSize(1)
        val link = items[0].querySelector("a")
        assertThat(link.textContent()).isEqualTo(featureName)
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
        querySelector("#description").fill(featureDescription.toString())
        locator("button[type='submit']").click()
        waitForCondition { querySelector("#feature h2") != null || querySelector("#error .error") != null }
    }
}
