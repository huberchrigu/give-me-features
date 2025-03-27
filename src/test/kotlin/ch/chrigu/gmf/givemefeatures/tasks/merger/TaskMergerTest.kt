package ch.chrigu.gmf.givemefeatures.tasks.merger

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TaskMergerTest {
    @ParameterizedTest
    @CsvSource(
        "description, new, new",
        "new, description, new",
        "new, new,"
    )
    fun testMerge(newVersionDescription: String, persistedNewVersionDescription: String, expectedDescription: String?) {
        val base = Task(TaskId("1"), "Name", Html("description"), version = 0)
        val testee = TaskMerger(base, change(base, newVersionDescription), change(base, persistedNewVersionDescription, version = 1))
        if (expectedDescription == null) {
            assertThrows<AbstractMerger.MergeFailedException> { testee.merge() }
        } else {
            assertThat(testee.merge().description.toString()).isEqualTo(expectedDescription)
        }
    }

    private fun change(base: Task, newVersionDescription: String, version: Long = base.version!!) =
        base.copy(description = Html(newVersionDescription), history = History(base.getCurrent()), version = version)
}
