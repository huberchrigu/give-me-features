package ch.chrigu.gmf.tasks.web.ui

import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskStatus

class TaskDetails(val id: TaskId, val name: String, val description: String, val status: TaskStatus, availableStatusActions: List<TaskStatus>, val version: Long) {
    val availableStatusActions = availableStatusActions.map { AvailableStatusAction(it) }
    val uri
        get() = "/tasks/${id}"

    class AvailableStatusAction(val status: TaskStatus) {
        val label = status.name
    }
}