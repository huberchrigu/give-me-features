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

    /**
     * Use it like this:
     * ```
     * taskRepository.findById(id).mergeWith(newVersion)
     * ```
     * Then, if the repository version has the same version number as `newVersion`, keep the `newVersion`.
     * Otherwise, try to merge them based on retrieved version for version number `newVersion.version`.
     */
    fun mergeWith(newVersion: Task, retrieveVersion: (Int) -> Task): Task {
        val oldVersion = this
        require(newVersion.version != null && oldVersion.version != null) { "Can only merge versioned tasks" }
        require(newVersion.version <= oldVersion.version) { "New version $newVersion should be equal or smaller than older version $oldVersion.version" }
        if (newVersion.version == oldVersion.version) return newVersion
        val base = retrieveVersion(newVersion.version)
        return TaskMerger(base, newVersion, oldVersion).merge()
    }

    fun block(): Task {
        check(status == TaskStatus.OPEN) { "Only open task can be blocked" }
        return copy(status = TaskStatus.BLOCKED)
    }

    fun reopen(): Task {
        check(status != TaskStatus.OPEN) { "Task is already open" }
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
