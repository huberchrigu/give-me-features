package ch.chrigu.gmf.tasks.web.ui

import ch.chrigu.gmf.plugins.PluginForm
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskStatus

class TaskDetails(val id: TaskId, val name: String, val description: String, val status: TaskStatus, availableStatusActions: List<TaskStatus>, val version: Long) {
    val availableStatusActions = availableStatusActions.map { AvailableStatusAction(it) }
    val uri
        get() = "/tasks/${id}"
    val statusClass = status.toCssClass()

    class AvailableStatusAction(val status: TaskStatus) {
        val label = status.name
        val statusClass = status.toCssClass()
    }

    suspend fun withPlugins(plugins: suspend (TaskId) -> List<PluginForm<*>>) = TaskDetailsWithPlugins(this, plugins(id))
}

class TaskDetailsWithPlugins(val task: TaskDetails, val plugins: List<PluginForm<*>>)

fun Task.toDetails() = TaskDetails(id, name, description.toHtml(), status, getAvailableStatus(), version!!)
private fun TaskStatus.toCssClass() = when (this) {
    TaskStatus.OPEN -> "bg-info"
    TaskStatus.BLOCKED -> "bg-danger"
    TaskStatus.DONE -> "bg-success"
}