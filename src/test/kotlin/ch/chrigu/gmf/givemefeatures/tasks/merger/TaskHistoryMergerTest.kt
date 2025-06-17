package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus
import ch.chrigu.gmf.givemefeatures.tasks.copy
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
