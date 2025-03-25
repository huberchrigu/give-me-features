package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.history.TaskSnapshot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TaskTest {
    private val taskId = TaskId("1")
    private val originalName = "name"
    private val newVersion = Task(taskId, "name2", Html("description"), TaskStatus.BLOCKED, 0)

    @Test
    fun `should use new version because version number is the same`() {
        val result = mergeWith(0)
        assertThat(result).isEqualTo(newVersion)
    }


    @Test
    fun `should merge versions because persisted version number is greater`() {
        val firstSnapshot = TaskSnapshot(originalName, Html("description"), TaskStatus.OPEN, 0)
        val result = mergeWith(1, History(firstSnapshot))
        assertThat(result).isEqualTo(
            Task(
                taskId, "name2", Html(""), TaskStatus.BLOCKED, 1,
                History(firstSnapshot, TaskSnapshot(originalName, Html(""), TaskStatus.OPEN, 1L))
            )
        )
    }

    private fun mergeWith(persistedVersion: Long, history: History<TaskSnapshot> = History()): Task {
        val oldVersion = Task(taskId, originalName, version = persistedVersion, history = history)
        val result = oldVersion.mergeWith(newVersion)
        return result
    }
}
