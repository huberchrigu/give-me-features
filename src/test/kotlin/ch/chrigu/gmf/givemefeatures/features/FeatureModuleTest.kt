package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskLinkedItem
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class FeatureModuleTest(private val featureService: FeatureService, private val featureRepository: FeatureRepository, @MockBean private val taskService: TaskService) {
    private val id = FeatureId("1")
    private val name = "name"
    private val description = "description"

    @BeforeEach
    fun resetDb() {
        runBlocking {
            featureRepository.deleteAll()
            featureRepository.save(Feature(id, name, description, emptyList()))
        }
    }

    @Test
    fun `should describe a feature`() {
        runBlocking {
            val result = featureService.newFeature(Feature.describeNewFeature("Login", "A user should be able to login"))
            assertThat(result.name).isEqualTo(result.name)
            assertThat(result.description).isEqualTo(result.description)
            assertThat(result.id).isNotNull()
        }
    }

    @Test
    fun `should add a task to a feature`() {
        runBlocking {
            val task = Task.describeNewTask("Task name")
            whenever(taskService.newTask(task)) doReturn task.copy(id = TaskId("1"))
            val result = featureService.addTask(id, task)
            assertThat(result!!.tasks).hasSize(1)
            verify(taskService).newTask(task)

            assertThat(featureService.addTask(FeatureId("0"), task)).isNull()
        }
    }

    @Test
    fun `should get a feature by id`() {
        runBlocking {
            assertThat(featureService.getFeature(FeatureId("0"))).isNull()
            assertThat(featureService.getFeature(FeatureId("1"))).isNotNull()
        }
    }

    @Test
    fun `should get all features`() {
        runBlocking {
            assertThat(featureService.getFeatures().toList()).hasSize(1)
        }
    }

    @Test
    fun `should find linked items`() {
        runBlocking {
            val taskId = TaskId("123")
            featureService.newFeature(Feature(id, name, description, listOf(taskId)))
            assertThat(featureService.getFor(taskId).toList()).containsExactly(TaskLinkedItem(id, name))
        }
    }
}
