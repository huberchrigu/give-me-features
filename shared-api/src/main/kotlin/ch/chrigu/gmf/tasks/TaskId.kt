package ch.chrigu.gmf.tasks

import java.util.UUID

data class TaskId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString() = id
}