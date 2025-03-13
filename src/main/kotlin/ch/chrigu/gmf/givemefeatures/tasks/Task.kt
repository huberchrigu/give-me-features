package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.merger.TaskMerger
import org.springframework.data.annotation.Version

data class Task(
    val id: TaskId? = null, val name: String, val description: Html = Html(""),
    val status: TaskStatus = TaskStatus.OPEN, @field:Version val version: Int? = null
) {

    fun update(update: TaskUpdate): Task {
        check(id != null && version != null) { "ID and version should already be set" }
        return update.apply(this)
    }

    fun mergeWith(updateBasedOnVersion: Int, update: Task.() -> Task, retrieveVersion: (Int) -> Task): Task {
        val oldVersion = this
        val newVersion = update(oldVersion).copy(version = updateBasedOnVersion)
        require(newVersion.version != null && oldVersion.version != null) { "Can only merge versioned tasks" }
        require(newVersion.version <= oldVersion.version){""}
        if (newVersion.version == oldVersion.version) return this
        val base = retrieveVersion(newVersion.version)
        return TaskMerger(base, this, oldVersion).merge()
    }

    fun block(): Task {
        check(status == TaskStatus.OPEN)
        return copy(status = TaskStatus.BLOCKED)
    }

    fun reopen(): Task {
        check(status == TaskStatus.DONE)
        return copy(status = TaskStatus.OPEN)
    }

    fun close(): Task {
        check(status != TaskStatus.DONE)
        return copy(status = TaskStatus.DONE)
    }

    companion object {
        fun describeNewTask(name: String) = Task(name = name)
    }

    data class TaskUpdate(val name: String, val description: Html) {
        fun apply(task: Task) = task.copy(name = name, description = description)
    }
}

enum class TaskStatus { OPEN, BLOCKED, DONE }

@JvmInline
value class TaskId(private val id: String) {
    override fun toString() = id
}
