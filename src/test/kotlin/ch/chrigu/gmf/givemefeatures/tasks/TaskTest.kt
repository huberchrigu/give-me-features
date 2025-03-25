package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.history.TaskSnapshot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TaskTest {
    private val taskId = TaskId("1")
    private val newVersion = Task(taskId, "name2", Html("description"), TaskStatus.BLOCKED, 1)

    @Test
    fun `should use new version because version number is the same`() {
        val result = mergeWith(1)
        assertThat(result).isEqualTo(newVersion)
    }

    @Test
    fun `should merge versions because persisted version number is greater`() {
        val result = mergeWith(2, listOf(TaskSnapshot("name", Html("description"), TaskStatus.OPEN, 1)))
        assertThat(result).isEqualTo(Task(taskId, "name2", Html(""), TaskStatus.BLOCKED, 2))
    }

    private fun mergeWith(persistedVersion: Long, history: List<TaskSnapshot> = emptyList()): Task {
        val oldVersion = Task(taskId, "name", version = persistedVersion, history = history)
        val result = oldVersion.mergeWith(newVersion)
        return result
    }
}
