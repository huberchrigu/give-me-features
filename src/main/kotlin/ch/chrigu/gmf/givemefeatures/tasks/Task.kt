package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.Html

data class Task(val id: TaskId? = null, val name: String, val description: Html = Html(""), val status: TaskStatus = TaskStatus.OPEN) {
    fun update(update: TaskUpdate) = update.apply(this)

    companion object {
        fun describeNewTask(name: String) = Task(name = name)
    }

    data class TaskUpdate(val name: String, val description: Html) {
        fun apply(task: Task) = task.copy(name = name, description = description)
    }
}

enum class TaskStatus { OPEN, DONE }

@JvmInline
value class TaskId(private val id: String) {
    override fun toString() = id
}
