package ch.chrigu.gmf.givemefeatures.tasks.history

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.Snapshot
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus

data class TaskSnapshot(private val name: String, private val description: Html, private val status: TaskStatus, override val version: Long) :
    Snapshot<TaskSnapshot, Task, TaskId> {
    override fun revertFrom(id: TaskId, history: List<TaskSnapshot>) = Task(id, name, description, status, version, history)
}
