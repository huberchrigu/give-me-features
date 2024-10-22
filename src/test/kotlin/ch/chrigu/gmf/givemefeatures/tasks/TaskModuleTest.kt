package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class TaskModuleTest(private val taskService: TaskService, private val taskRepository: TaskRepository) {
    @Test
    fun `should describe a new task`() {
        runBlocking {
            taskService.newTask(Task.describeNewTask("new task"))
            val tasks = taskRepository.findAll().toList()
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].name).isEqualTo("new task")
            assertThat(tasks[0].id).isNotNull
        }
    }

    @Test
    fun `should resolve tasks`() {
        runBlocking {
            val task = taskRepository.save(Task.describeNewTask("test"))
            val result = taskService.resolve(listOf(task.id!!)).toList()
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("test")
        }
    }
}