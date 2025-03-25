package ch.chrigu.gmf.givemefeatures.tasks.history

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.Snapshot
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus

data class TaskSnapshot(val name: String, val description: Html, val status: TaskStatus, override val version: Long) : Snapshot<TaskSnapshot> {
    override fun withVersion(version: Long): TaskSnapshot {
        return copy(version = version)
    }
}
