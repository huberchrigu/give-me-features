package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.web.UiTest
import ch.chrigu.gmf.givemefeatures.tasks.*
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@UiTest(TaskController::class)
class TaskControllerUiTest(@MockkBean private val taskService: TaskService) {
    private val taskId = TaskId("1")
    private val name = "My task"
    private val description = "desc"
    private val descriptionHtml = Html("<p>$description</p>")
    private val newName = "Updated task"
    private val newDescription = "Updated desc"
    private val newDescriptionHtml = Html("<p>$newDescription</p>")
    private val featureName = "Feature"
    private val featureId = FeatureId("featureId")

    @LocalServerPort
    private var port: Int = 0

    @Test
    fun `should show and update task`() {
        withTask()
        withUpdate()

        openTaskPage {
            assertTask()
            clickEdit()
            assertForm()
            submitTaskForm()
            assertTask(newName, newDescription)
        }
    }

    @Test
    fun `should cancel update task`() {
        withTask()

        openTaskPage {
            assertTask()
            clickEdit()
            cancelTaskForm()
        }
    }

    @Test
    fun `should change status`() {
        withTask()
        coEvery { taskService.blockTask(taskId) } returns Task(taskId, name, descriptionHtml, TaskStatus.BLOCKED)

        openTaskPage {
            assertTask()
            changeStatus(newStatus = TaskStatus.BLOCKED)
            assertTask(status = TaskStatus.BLOCKED)
        }
    }

    @Test
    fun `should display feature page`() {
        withTask()

        openTaskPage {
            assertTask()
            clickFeature()
            assertFeature()
        }
    }

    @Test
    fun `cannot show task`() {
        withNoTask()

        openTaskPage {
            assertError("Task $taskId not found")
        }
    }

    private fun Page.cancelTaskForm() {
        querySelectorAll("#task button")[1].click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.assertError(prefix: String) {
        val error = querySelector("#error p")
        assertThat(error.textContent()).startsWith(prefix)
    }

    private fun Page.clickEdit() {
        getButton().click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.getButton() = querySelectorAll("#task button").first { it.textContent() == "Edit" }

    private fun Page.assertForm() {
        val nameInput = querySelector("#name")
        val descriptionInput = querySelector("#description")
        val buttons = querySelectorAll("#task button.btn")
        assertThat(nameInput.inputValue()).isEqualTo(name)
        assertThat(descriptionInput.inputValue()).isEqualTo(descriptionHtml.toString())
        assertThat(buttons).hasSize(2)
        assertThat(buttons[0].textContent()).isEqualTo("Submit")
        assertThat(buttons[1].textContent()).isEqualTo("Cancel")
    }

    private fun Page.submitTaskForm() {
        querySelector("#name").fill(newName)
        frames()[1].querySelector("body#tinymce").fill(newDescription)
        querySelector("#task button.btn").click()
        waitForLoadState(LoadState.NETWORKIDLE)
        waitForCondition { querySelector("#task h1") != null }
    }

    private fun Page.changeStatus(status: TaskStatus = TaskStatus.OPEN, newStatus: TaskStatus) {
        val statusElement = querySelector(".status-${status.name}")
        statusElement.click()
        val statusActions = querySelector("#status-actions")
        waitForCondition { statusActions.getAttribute("class").contains("show") }
        val newStatusElement = statusActions.querySelectorAll("button").first { it.textContent() == newStatus.name }
        newStatusElement.click()
        waitForLoadState(LoadState.NETWORKIDLE)
        waitForCondition { querySelector("h1 .status-${newStatus.name}") != null }
    }

    private fun Page.assertTask(expectedName: String = name, expectedDescription: String = description, status: TaskStatus = TaskStatus.OPEN) {
        assertNameAndDescription(expectedName, expectedDescription, status)
        assertLinks()
        assertStatus(status)
    }

    private fun Page.assertStatus(status: TaskStatus) {
        val statusElement = querySelector("h1 .status-${status.name}")
        assertThat(statusElement.textContent()).isEqualTo(status.name)
    }

    private fun Page.assertLinks() {
        val links = querySelectorAll("ul li a")
        assertThat(links).hasSize(1)
        assertThat(links[0].textContent()).isEqualTo(featureName)
    }

    private fun Page.assertNameAndDescription(expectedName: String, expectedDescription: String, expectedStatus: TaskStatus) {
        val title = querySelector("#task h1").textContent()
        val description = querySelector("#task p").textContent()
        assertThat(title).isEqualTo("$expectedName $expectedStatus")
        assertThat(description).isEqualTo(expectedDescription)
    }

    private fun Page.assertFeature() {
        assertThat(url()).isEqualTo("http://localhost:$port/features/$featureId")
    }

    private fun Page.clickFeature() {
        querySelector("ul li a").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun withUpdate() {
        coEvery { taskService.updateTask(taskId, Task.TaskUpdate(newName, newDescriptionHtml)) } returns Task(taskId, newName, newDescriptionHtml)
    }

    private fun withTask() {
        coEvery { taskService.getTask(taskId) } returns Task(taskId, name, descriptionHtml)
        every { taskService.getLinkedItems(taskId) } returns flowOf(TaskLinkedItem(featureId, featureName))
    }

    private fun withNoTask() {
        coEvery { taskService.getTask(taskId) } throws TaskNotFoundException(taskId)
    }

    private fun openTaskPage(test: Page.() -> Unit) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch()
            val page = browser.newPage()
            page.navigate("http://localhost:$port/tasks/$taskId")

            test(page)

            browser.close()
        }
    }
}
