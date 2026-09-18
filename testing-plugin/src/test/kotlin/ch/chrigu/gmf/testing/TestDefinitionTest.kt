package ch.chrigu.gmf.testing

import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.plugins.TaskReferenceId
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestDefinitionTest {
    @Test
    fun `should create test reference with feature`() {
        val featureId = mockk<FeatureReferenceId>()
        val ref = TestReference(feature = featureId)
        assertThat(ref.feature).isEqualTo(featureId)
        assertThat(ref.task).isNull()
    }

    @Test
    fun `should create test reference with task`() {
        val taskId = mockk<TaskReferenceId>()
        val ref = TestReference(task = taskId)
        assertThat(ref.task).isEqualTo(taskId)
        assertThat(ref.feature).isNull()
    }

    @Test
    fun `should throw when both or neither feature and task are set`() {
        assertThrows<IllegalArgumentException> {
            TestReference(feature = null, task = null)
        }
        assertThrows<IllegalArgumentException> {
            val featureId = mockk<FeatureReferenceId>()
            val taskId = mockk<TaskReferenceId>()
            TestReference(feature = featureId, task = taskId)
        }
    }

    @Test
    fun `should create test definition with defaults`() {
        val testDef = TestDefinition(id = "123")
        assertThat(testDef.id).isEqualTo("123")
        assertThat(testDef.tests).isEmpty()
        assertThat(testDef.state).isEqualTo(TestState.NOT_STARTED)
    }
}
