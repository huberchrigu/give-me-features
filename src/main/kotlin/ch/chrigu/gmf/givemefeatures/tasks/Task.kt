package ch.chrigu.gmf.givemefeatures.tasks

data class Task(val id: TaskId? = null, val name: String, val description: String = "", val status: TaskStatus = TaskStatus.OPEN) {
    companion object {
        fun describeNewTask(name: String) = Task(name = name)
    }
}

enum class TaskStatus { OPEN, DONE }

@JvmInline
value class TaskId(val id: String)