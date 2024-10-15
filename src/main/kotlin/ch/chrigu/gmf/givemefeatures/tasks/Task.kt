package ch.chrigu.gmf.givemefeatures.tasks

import java.util.UUID

data class Task(val id: UUID? = null, val name: String, val description: String = "", val status: TaskStatus = TaskStatus.OPEN)
enum class TaskStatus { OPEN, DONE }