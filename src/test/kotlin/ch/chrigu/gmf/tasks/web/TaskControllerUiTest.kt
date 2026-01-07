package ch.chrigu.gmf.tasks.web

import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.web.UiTest
import ch.chrigu.gmf.tasks.*
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.LoadState
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@UiTest(TaskController::class)
class TaskControllerUiTest(@MockkBean private val taskService: TaskService) {
    private val taskId = TaskId("1")
    private val name = "My task"
    private val description = "desc"
    private val descriptionMarkdown = Markdown(description)
    private val newName = "Updated task"
    private val newDescription = "Updated desc"
    private val newDescriptionMarkdown = Markdown(newDescription)
    private val featureName = "Feature"
    private val featureId = FeatureId("featureId")
    private val linkableFeature = "Linkable feature"

    private lateinit var page: Page

    private val taskUpdates = MutableSharedFlow<Task>()

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
    fun `should show external change`() {
        withTask()

        openTaskPage {
            assertTask()
            externalTaskChange(newName, newDescriptionMarkdown, TaskStatus.BLOCKED)
            assertTask(newName, newDescription, TaskStatus.BLOCKED)
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
        coEvery { taskService.blockTask(taskId, 0) } returns Task(taskId, 1, name, descriptionMarkdown, TaskStatus.BLOCKED)

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
    fun `should link feature and unlink other feature`() {
        withTask()
        withLinkableFeature()

        openTaskPage {
            assertTask()
            clickPlusButton()
            searchFeature()
            chooseFeature()
            assertTask(linkedFeatures = listOf(featureName, linkableFeature))
            unlinkFeature()
            assertTask(linkedFeatures = listOf(linkableFeature))
        }
    }

    @Test
    fun `should cancel link feature`() {
        withTask()
        withLinkableFeature()

        openTaskPage {
            assertTask()
            clickPlusButton()
            searchFeature()
            cancelLinkFeature()
            assertTask(linkedFeatures = listOf(featureName))
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
        val error = querySelector("#error div:nth-child(2)")
        assertThat(error.textContent()).startsWith(prefix)
    }

    private fun Page.clickEdit() {
        getButton().click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun Page.getButton() = querySelectorAll("#task button").first { it.textContent().contains("Edit Task") }

    private fun Page.clickPlusButton() {
        querySelector(".btn[hx-target=\\#link-feature]").click()
        waitForCondition { querySelector("#item-selector input") != null }
    }

    private fun Page.searchFeature() {
        querySelector("#item-selector input").fill(linkableFeature.substring(1, 3))
        waitForCondition { querySelector("#item-list a") != null }
    }

    private fun Page.chooseFeature() {
        querySelector("#item-list a").click()
        waitForCondition { querySelector("#item-list a") == null }
    }

    private fun Page.cancelLinkFeature() {
        querySelector("#item-selector button").click()
        waitForCondition { querySelector("#item-list a") == null }
    }

    private fun Page.unlinkFeature() {
        querySelector("#linked-features button").click()
        waitForCondition { querySelectorAll("#linked-features div.list-group-item").size == 1 }
    }

    private fun Page.assertForm() {
        val nameInput = querySelector("#name")
        val descriptionInput = querySelector("#description")
        val buttons = querySelectorAll("#task button.btn")
        assertThat(nameInput.inputValue()).isEqualTo(name)
        assertThat(descriptionInput.inputValue()).isEqualTo(descriptionMarkdown.toString())
        assertThat(buttons).hasSize(2)
        assertThat(buttons[0].textContent()).isEqualTo("Submit")
        assertThat(buttons[1].textContent()).isEqualTo("Cancel")
    }

    private fun Page.submitTaskForm() {
        querySelector("#name").fill(newName)
        querySelector("#description").fill(newDescriptionMarkdown.toString())
        querySelector("#task button.btn").click()
        waitForLoadState(LoadState.NETWORKIDLE)
        waitForCondition { querySelector("form#task") == null }
    }

    private fun Page.changeStatus(status: TaskStatus = TaskStatus.OPEN, newStatus: TaskStatus) {
        val statusElement = querySelector("div.dropdown button.btn")
        statusElement.click()
        val newStatusElement = querySelectorAll("button.dropdown-item").first { it.textContent().contains(newStatus.name) }
        newStatusElement.click()
        waitForCondition { querySelector("div.dropdown span:nth-child(2)").textContent() == newStatus.name }
    }

    private fun Page.externalTaskChange(name: String, description: Markdown, status: TaskStatus) = runBlocking {
        taskUpdates.emit(Task(taskId, 1L, name, description, status))
        waitForCondition { querySelector("#task div.dropdown span:nth-child(2)").textContent() == status.name }
    }

    private fun Page.assertTask(
        expectedName: String = name,
        expectedDescription: String = description,
        status: TaskStatus = TaskStatus.OPEN,
        linkedFeatures: List<String> = listOf(featureName)
    ) {
        assertNameAndDescription(expectedName, expectedDescription, status)
        assertLinks(linkedFeatures)
        assertStatus(status)
    }

    private fun Page.assertStatus(status: TaskStatus) {
        val statusElement = querySelector(".dropdown span:nth-child(2)")
        assertThat(statusElement.textContent()).isEqualTo(status.name)
    }

    private fun Page.assertLinks(features: List<String> = listOf(featureName)) {
        val links = querySelectorAll("#linked-features div.list-group-item a")
        assertThat(links).hasSize(features.size)
        features.forEachIndexed { index, feature ->
            assertThat(links[index].textContent()).isEqualTo(feature)
        }
    }

    private fun Page.assertNameAndDescription(expectedName: String, expectedDescription: String, expectedStatus: TaskStatus) {
        val title = querySelector("#task div.card-header h3").textContent()
        val status = querySelector("#task button span:nth-child(2)").textContent()
        val description = querySelector("#task p").textContent()
        assertThat(title).isEqualTo(expectedName)
        assertThat(status).isEqualTo(expectedStatus.toString())
        assertThat(description).isEqualTo(expectedDescription)
    }

    private fun Page.assertFeature() {
        assertThat(url()).isEqualTo("http://localhost:$port/features/$featureId")
    }

    private fun Page.clickFeature() {
        querySelector("#linked-features div.list-group-item a").click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun withUpdate() {
        coEvery { taskService.updateTask(taskId, 0, Task.TaskUpdate(newName, newDescriptionMarkdown)) } returns Task(taskId, 1, newName, newDescriptionMarkdown, TaskStatus.OPEN)
    }

    private fun withTask() {
        coEvery { taskService.getTask(taskId) } returns Task(taskId, 0, name, descriptionMarkdown, TaskStatus.OPEN)
        every { taskService.getLinkedItems(taskId) } returns flowOf(TaskLinkedItem(featureId, featureName, 0))
        coEvery { taskService.getTaskUpdates(taskId) } returns taskUpdates.asSharedFlow()
    }

    private fun withNoTask() {
        coEvery { taskService.getTask(taskId) } throws TaskNotFoundException(taskId)
    }

    private fun openTaskPage(test: Page.() -> Unit) {
        page.navigate("http://localhost:$port/tasks/$taskId")
        page.login()
        test(page)
    }

    private fun Page.login() {
        querySelector("input[name='username']").fill("user")
        querySelector("input[name='password']").fill("user")
        querySelector("button[type='submit']").click()
    }

    private fun withLinkableFeature() {
        every { taskService.getLinkableItems(taskId, linkableFeature.substring(1, 3)) } returns flowOf(TaskLinkedItem(linkableFeature, linkableFeature, 0))
        coEvery { taskService.linkTo(taskId, linkableFeature, 0) } returns flowOf(TaskLinkedItem(featureId, featureName, 0), TaskLinkedItem(linkableFeature, linkableFeature, 0))
        coEvery { taskService.unlink(taskId, featureId.toString(), 0) } returns flowOf(TaskLinkedItem(linkableFeature, linkableFeature, 0))
    }
}
