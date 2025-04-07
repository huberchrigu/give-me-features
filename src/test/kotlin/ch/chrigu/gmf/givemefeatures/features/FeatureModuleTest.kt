package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class FeatureModuleTest(
    private val featureService: FeatureService, private val featureProviderService: FeatureProviderService,
    private val featureRepository: FeatureRepository, @MockkBean private val taskService: TaskService
) {
    private val id = FeatureId("1")
    private val name = "name"
    private val description = Html("description")

    @BeforeEach
    fun resetDb() = runTest {
        featureRepository.deleteAll()
        featureRepository.save(Feature(id, name, description, emptyList(), null))
    }

    @Test
    fun `should describe a feature`() = runTest {
        val result = featureService.newFeature(Feature.describeNewFeature("Login", Html("A user should be able to login")))
        assertThat(result.name).isEqualTo(result.name)
        assertThat(result.description).isEqualTo(result.description)
        assertThat(result.id).isNotNull()
    }

    @Test
    fun `should add a task to a feature`() = runTest {
        val task = Task.describeNewTask("Task name")
        coEvery { taskService.newTask(task) } returns Task(TaskId("1"), 0, "Task name", Html(""), TaskStatus.OPEN)
        val result = featureService.addTask(id, 0, task)
        assertThat(result!!.tasks).hasSize(1)
        coVerify { taskService.newTask(task) }

        assertThrows<FeatureNotFoundException> { featureService.addTask(FeatureId("0"), 0, task) }
    }

    @Test
    fun `should get a feature by id`() = runTest {
        assertThrows<FeatureNotFoundException> { assertThat(featureService.getFeature(FeatureId("0"))) }
        assertThat(featureService.getFeature(FeatureId("1"))).isNotNull()
    }

    @Test
    fun `should get all features`() = runTest {
        assertThat(featureService.getFeatures().toList()).hasSize(1)
    }

    @Test
    fun `should find linked items`() = runTest {
        val taskId = TaskId("123")
        featureService.newFeature(Feature(id, name, description, listOf(taskId), version = 0))
        assertThat(featureProviderService.getFor(taskId).toList()).containsExactly(TaskLinkedItem(id, name))
    }

    @Test
    fun `should update feature`() = runTest {
        val newDescription = Html("<p>updated</p>")
        val newName = "new name"
        val result = featureService.updateFeature(id, 0L, FeatureUpdate(newName, newDescription))
        assertThat(result.name).isEqualTo(newName)
        assertThat(result.description).isEqualTo(newDescription)
        val persisted = featureService.getFeature(id)
        assertThat(persisted.name).isEqualTo(newName)
        assertThat(persisted.description).isEqualTo(newDescription)
    }
}
