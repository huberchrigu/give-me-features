package ch.chrigu.gmf.testing.plugin

import ch.chrigu.gmf.plugins.FeatureReference
import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.plugins.ItemType
import ch.chrigu.gmf.plugins.TaskReference
import ch.chrigu.gmf.plugins.TaskReferenceId
import ch.chrigu.gmf.testing.TestDefinition
import ch.chrigu.gmf.testing.TestState
import ch.chrigu.gmf.testing.persistence.TestDefinitionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TestingTouchpointFactoryTest {
    private val repository = mockk<TestDefinitionRepository>()
    private val factory = TestingTouchpointFactory(repository)

    private data class TestFeatureId(private val id: String) : FeatureReferenceId {
        override fun toString() = id
    }

    private data class TestTaskId(private val id: String) : TaskReferenceId {
        override fun toString() = id
    }

    @Test
    fun `should create touchpoints for feature and task`() {
        val touchpoints = factory.create()
        assertThat(touchpoints.featureItem).isNotNull
        assertThat(touchpoints.taskItem).isNotNull

        val featureItem = touchpoints.featureItem!!
        assertThat(featureItem.persistenceClass).isEqualTo(TestDefinition::class.java)
        assertThat(featureItem.fields).hasSize(1)
        assertThat(featureItem.fields[0].id).isEqualTo("state")
        assertThat(featureItem.fields[0].type).isEqualTo(ItemType.TEXT)
        assertThat(featureItem.fields[0].title).isEqualTo("State")

        val taskItem = touchpoints.taskItem!!
        assertThat(taskItem.persistenceClass).isEqualTo(TestDefinition::class.java)
        assertThat(taskItem.fields).hasSize(1)
        assertThat(taskItem.fields[0].id).isEqualTo("state")
        assertThat(taskItem.fields[0].type).isEqualTo(ItemType.TEXT)
        assertThat(taskItem.fields[0].title).isEqualTo("State")
    }

    @Test
    fun `should map form values to test definition for feature`() {
        val touchpoints = factory.create()
        val featureId = TestFeatureId("feat-123")
        val feature = mockk<FeatureReference> {
            every { id } returns featureId
        }

        val fromMap = touchpoints.featureItem!!.fromMap
        val testDef = mapOf("state" to listOf("RUNNING")).fromMap(feature) as TestDefinition

        assertThat(testDef.id).isEqualTo("feat-123")
        assertThat(testDef.state).isEqualTo(TestState.RUNNING)
        assertThat(testDef.tests).hasSize(1)
        assertThat(testDef.tests[0].feature).isEqualTo(featureId)
        assertThat(testDef.tests[0].task).isNull()
    }

    @Test
    fun `should map form values to test definition for task`() {
        val touchpoints = factory.create()
        val taskId = TestTaskId("task-456")
        val task = mockk<TaskReference> {
            every { id } returns taskId
        }

        val fromMap = touchpoints.taskItem!!.fromMap
        val testDef = mapOf("state" to listOf("FINISHED")).fromMap(task) as TestDefinition

        assertThat(testDef.id).isEqualTo("task-456")
        assertThat(testDef.state).isEqualTo(TestState.FINISHED)
        assertThat(testDef.tests).hasSize(1)
        assertThat(testDef.tests[0].task).isEqualTo(taskId)
        assertThat(testDef.tests[0].feature).isNull()
    }

    @Test
    fun `should default to NOT_STARTED for unknown state value`() {
        val touchpoints = factory.create()
        val featureId = TestFeatureId("feat-123")
        val feature = mockk<FeatureReference> {
            every { id } returns featureId
        }

        val fromMap = touchpoints.featureItem!!.fromMap
        val testDef = emptyMap<String, List<String>>().fromMap(feature) as TestDefinition
        assertThat(testDef.state).isEqualTo(TestState.NOT_STARTED)
    }

    @Test
    fun `should reset state to NOT_STARTED on feature trigger`() = runTest {
        val touchpoints = factory.create()
        val featureId = TestFeatureId("feat-1")
        val feature = mockk<FeatureReference> {
            every { id } returns featureId
        }

        val existing = TestDefinition("feat-1", emptyList(), TestState.FINISHED)
        coEvery { repository.findById("feat-1") } returns existing
        val savedSlot = slot<TestDefinition>()
        coEvery { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        touchpoints.featureItem!!.triggers.onChange(feature)

        coVerify { repository.save(any()) }
        assertThat(savedSlot.captured.state).isEqualTo(TestState.NOT_STARTED)
    }

    @Test
    fun `should reset state to NOT_STARTED on task trigger`() = runTest {
        val touchpoints = factory.create()
        val taskId = TestTaskId("task-1")
        val task = mockk<TaskReference> {
            every { id } returns taskId
        }

        val existing = TestDefinition("task-1", emptyList(), TestState.RUNNING)
        coEvery { repository.findById("task-1") } returns existing
        val savedSlot = slot<TestDefinition>()
        coEvery { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        touchpoints.taskItem!!.triggers.onChange(task)

        coVerify { repository.save(any()) }
        assertThat(savedSlot.captured.state).isEqualTo(TestState.NOT_STARTED)
    }

    @Test
    fun `should configure auto configuration correctly`() {
        val autoConfig = TestingPluginAutoConfiguration()
        val plugin = autoConfig.testingPlugin(repository)
        assertThat(plugin.id.toString()).isEqualTo("testing")
        assertThat(plugin.title).isEqualTo("Testing")
        assertThat(plugin.touchpoints.featureItem).isNotNull
        assertThat(plugin.touchpoints.taskItem).isNotNull
    }
}
