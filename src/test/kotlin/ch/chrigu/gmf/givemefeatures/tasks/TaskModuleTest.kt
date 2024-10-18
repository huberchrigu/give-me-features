package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class TaskModuleTest(private val taskService: TaskService) {
    @Test
    fun `should describe a new task`() {
        TODO()
    }

    @Test
    fun `should resolve tasks`() {
        TODO()
    }
}