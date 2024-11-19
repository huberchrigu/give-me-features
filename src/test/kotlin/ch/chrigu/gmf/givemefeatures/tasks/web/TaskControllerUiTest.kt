package ch.chrigu.gmf.givemefeatures.tasks.web

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
    private val newName = "Updated task"
    private val newDescription = "Updated desc"
    private val featureName = "Feature"
    private val featureId = "featureId"

    @LocalServerPort
    private var port: Int = 0

    /**
     * Fails because:
     * ```
     * no answer found for: TaskService(ch.chrigu.gmf.givemefeatures.tasks.TaskService#0 bean#1).update-gBXuGgk(1, TaskUpdate(name=Updated task, description=Updated desc), continuation {})
     * ```
     * This seems to be a mockk issue.
     */
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
        querySelector("#task button").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.assertForm() {
        val nameInput = querySelector("#name")
        val descriptionInput = querySelector("#description")
        val buttons = querySelectorAll("#task button")
        assertThat(nameInput.inputValue()).isEqualTo(name)
        assertThat(descriptionInput.inputValue()).isEqualTo(description)
        assertThat(buttons).hasSize(2)
        assertThat(buttons[0].textContent()).isEqualTo("Submit")
        assertThat(buttons[1].textContent()).isEqualTo("Cancel")
    }

    private fun Page.submitTaskForm() {
        querySelector("#name").fill(newName)
        querySelector("#description").fill(newDescription)
        querySelector("#task button").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.assertTask(expectedName: String = name, expectedDescription: String = description) {
        val title = querySelector("#task h1").textContent()
        val description = querySelector("#task p").textContent()
        assertThat(title).isEqualTo(expectedName)
        assertThat(description).isEqualTo(expectedDescription)
        val links = querySelectorAll("ul li a")
        assertThat(links).hasSize(1)
        assertThat(links[0].textContent()).isEqualTo(featureName)
    }

    private fun Page.assertFeature() {
        val title = querySelector("#feature h2").textContent()
        assertThat(title).isEqualTo(featureName)
    }

    private fun Page.clickFeature() {
        querySelector("ul li a").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun withUpdate() {
        coEvery { taskService.update(taskId, Task.TaskUpdate(newName, newDescription)) } returns Task(taskId, newName, newDescription)
    }

    private fun withTask() {
        coEvery { taskService.getTask(taskId) } returns Task(taskId, name, description)
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
