package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TaskTest {
    private val taskId = TaskId("1")

    private val oldVersion = Task(taskId, "name", version = 1)

    @Test
    fun `should use new version because version number is the same`() {
        val result = mergeWith(1) { fail() }
        assertThat(result).isEqualTo(oldVersion)
    }

    @Test
    fun `should merge versions because persisted version number is greater`() {
        val result = mergeWith(2) { Task(taskId, "name", Html("description"), TaskStatus.OPEN, it) }
        assertThat(result).isEqualTo(Task(taskId, "name2", Html(""), TaskStatus.BLOCKED, 2))
    }

    private fun mergeWith(persistedVersion: Int, retrieveVersion: (Int) -> Task): Task {
        val result = oldVersion.mergeWith(persistedVersion, { Task(taskId, "name2", Html("description"), TaskStatus.BLOCKED, persistedVersion) }, retrieveVersion)
        return result
    }
}
