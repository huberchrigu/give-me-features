package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import com.ninjasquad.springmockk.MockkBean
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class TaskModuleTest(private val taskService: TaskService, private val taskRepository: TaskRepository, @MockkBean private val linkedItemProvider: LinkedItemProvider) {
    @BeforeEach
    fun reset() = runTest {
        taskRepository.deleteAll()
    }

    @Test
    fun `should update description`() = runTest {
        val task = taskRepository.save(Task.describeNewTask("task"))
        val id = task.id!!
        val newDescription = Html("new description")
        taskService.updateTask(id, 0, Task.TaskUpdate("task", newDescription))
        assertThat(taskRepository.findById(id.toString())?.description).isEqualTo(newDescription)
    }

    @Test
    fun `should describe a new task`() = runTest {
        taskService.newTask(Task.describeNewTask("new task"))
        val tasks = taskRepository.findAll().toList()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].name).isEqualTo("new task")
        assertThat(tasks[0].id).isNotNull
    }

    @Test
    fun `should resolve tasks`() = runTest {
        val task = taskRepository.save(Task.describeNewTask("test"))
        val result = taskService.resolve(listOf(task.id!!)).toList()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("test")
    }

    /**
     * There should never be an optimistic locking exception.
     */
    @Test
    fun `should merge tasks`() = runTest {
        val task = taskRepository.save(Task.describeNewTask("test"))
        val askUpdate1 = async { taskService.updateTask(task.id!!, 0, Task.TaskUpdate("new task", Html("new description"))) }
        val askUpdate2 = async { taskService.blockTask(task.id!!, 0) }
        awaitAll(askUpdate1, askUpdate2)
        val resultIgnoreHistory = taskRepository.findById(task.id!!.toString())!!.copy(history = History())
        assertThat(resultIgnoreHistory).isEqualTo(Task(task.id, "new task", Html("new description"), TaskStatus.BLOCKED, 2))
    }

    @Test
    fun `should block, reopen and close task`() = runTest {
        val task = taskRepository.save(Task.describeNewTask("test"))
        val blocked = taskService.blockTask(task.id!!, task.version!!)
        assertThat(blocked.status).isEqualTo(TaskStatus.BLOCKED)
        val reopened = taskService.reopenTask(blocked.id!!, blocked.version!!)
        assertThat(reopened.status).isEqualTo(TaskStatus.OPEN)
        assertThat(taskService.closeTask(reopened.id!!, reopened.version!!).status).isEqualTo(TaskStatus.DONE)
    }
}
