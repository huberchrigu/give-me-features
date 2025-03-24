package ch.chrigu.gmf.givemefeatures.tasks.web.ui

import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus

class TaskDetails(val id: String, val name: String, val description: String, val status: TaskStatus, availableStatusActions: List<TaskStatus>) {
    val availableStatusActions = availableStatusActions.map { AvailableStatusAction(it) }

    class AvailableStatusAction(val status: TaskStatus) {
        val label = status.name
    }
}