package ch.chrigu.gmf.tasks.merger

import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.history.AbstractMerger
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskStatus
import ch.chrigu.gmf.tasks.copy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TaskHistoryMergerTest {
    @ParameterizedTest
    @CsvSource(
        "description, new, new",
        "new, description, new",
        "new, new,"
    )
    fun testMerge(mergingDescription: String, currentDescription: String, expectedDescription: String?) {
        val sharedVersion = Task(TaskId("1"), 0, "Name", Markdown("description"), TaskStatus.OPEN)
        val testee = TaskMerger()
        val mergingVersion = change(sharedVersion, mergingDescription)
        val currentVersion = change(sharedVersion, currentDescription, 1)
        if (expectedDescription == null) {
            assertThrows<AbstractMerger.MergeFailedException> {
                testee.merge(
                    sharedVersion,
                    mergingVersion,
                    currentVersion
                )
            }
        } else {
            assertThat(testee.merge(sharedVersion, mergingVersion, currentVersion).description.toString()).isEqualTo(
                expectedDescription
            )
        }
    }

    private fun change(base: Task, newVersionDescription: String, version: Long = base.version!!) =
        base.copy(description = Markdown(newVersionDescription), version = version)
}
