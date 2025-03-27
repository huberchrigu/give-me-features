package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.shared.history.Mergeable
import ch.chrigu.gmf.givemefeatures.tasks.history.TaskSnapshot
import ch.chrigu.gmf.givemefeatures.tasks.merger.TaskMerger
import org.springframework.data.annotation.Version

data class Task(
    override val id: TaskId? = null, val name: String, val description: Html = Html(""),
    val status: TaskStatus = TaskStatus.OPEN, @field:Version override val version: Long? = null, override val history: History<TaskSnapshot> = History()
) : Mergeable<TaskSnapshot, Task, TaskId> {

    fun update(update: TaskUpdate): Task {
        check(id != null && version != null) { "ID and version should already be set" }
        return update.apply(this)
    }

    fun block(): Task {
        check(getAvailableStatus().contains(TaskStatus.BLOCKED)) { "Only open task can be blocked" }
        return newVersion(copy(status = TaskStatus.BLOCKED))
    }

    fun reopen(): Task {
        check(getAvailableStatus().contains(TaskStatus.OPEN)) { "Task is already open" }
        return newVersion(copy(status = TaskStatus.OPEN))
    }

    fun close(): Task {
        check(getAvailableStatus().contains(TaskStatus.DONE)) { "Task is already closed" }
        return newVersion(copy(status = TaskStatus.DONE))
    }

    /**
     * The status to which this task can be changed to.
     */
    fun getAvailableStatus() = when (status) {
        TaskStatus.OPEN -> listOf(TaskStatus.BLOCKED, TaskStatus.DONE)
        TaskStatus.BLOCKED -> listOf(TaskStatus.OPEN, TaskStatus.DONE)
        TaskStatus.DONE -> listOf(TaskStatus.OPEN)
    }

    override fun getCurrent() = TaskSnapshot(name, description, status, version!!)
    override fun withSnapshot(snapshot: TaskSnapshot) = Task(id, snapshot.name, snapshot.description, snapshot.status, snapshot.version, history)
    override fun withHistory(history: History<TaskSnapshot>) = copy(history = history)
    override fun getMerger(base: Task, newVersion: Task, oldVersion: Task) = TaskMerger(base, newVersion, oldVersion)

    companion object {
        fun describeNewTask(name: String) = Task(name = name)
    }

    data class TaskUpdate(val name: String, val description: Html) {
        fun apply(task: Task) = task.newVersion(task.copy(name = name, description = description))
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
