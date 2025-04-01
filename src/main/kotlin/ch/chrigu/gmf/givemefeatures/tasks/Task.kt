package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AbstractAggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.Html

class Task(
    id: TaskId?, version: Long?, val name: String, val description: Html, val status: TaskStatus
) : AbstractAggregateRoot<TaskId>(id, version) {
    init {
        if (isNew()) {
            require(status == TaskStatus.OPEN)
        }
    }

    fun update(update: TaskUpdate): Task {
        check(!isNew()) { "ID and version should already be set" }
        return update.apply(this)
    }

    fun block(): Task {
        check(getAvailableStatus().contains(TaskStatus.BLOCKED)) { "Only open task can be blocked" }
        return updateState(status = TaskStatus.BLOCKED)
    }

    fun reopen(): Task {
        check(getAvailableStatus().contains(TaskStatus.OPEN)) { "Task is already open" }
        return updateState(status = TaskStatus.OPEN)
    }

    fun close(): Task {
        check(getAvailableStatus().contains(TaskStatus.DONE)) { "Task is already closed" }
        return updateState(status = TaskStatus.DONE)
    }

    /**
     * The status to which this task can be changed to.
     */
    fun getAvailableStatus() = when (status) {
        TaskStatus.OPEN -> listOf(TaskStatus.BLOCKED, TaskStatus.DONE)
        TaskStatus.BLOCKED -> listOf(TaskStatus.OPEN, TaskStatus.DONE)
        TaskStatus.DONE -> listOf(TaskStatus.OPEN)
    }

    private fun updateState(name: String = this.name, description: Html = this.description, status: TaskStatus = this.status) = Task(id, version, name, description, status)

    companion object {
        fun describeNewTask(name: String) = Task(null, null, name, Html(""), TaskStatus.OPEN)
    }

    data class TaskUpdate(val name: String, val description: Html) {
        fun apply(task: Task) = task.updateState(name = name, description = description)
    }
}

enum class TaskStatus {
    /**
     * The task is ready to be worked on. There are no blockers and it is not done yet.
     */
    OPEN,

    /**
     * The task progress is blocked by a blocker.
     */
    BLOCKED,

    /**
     * Task is completed.
     */
    DONE
}

@JvmInline
value class TaskId(private val id: String) {
    override fun toString() = id
}
