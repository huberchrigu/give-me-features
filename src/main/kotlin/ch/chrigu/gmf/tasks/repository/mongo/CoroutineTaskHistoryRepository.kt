package ch.chrigu.gmf.tasks.repository.mongo

import ch.chrigu.gmf.shared.history.History
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineTaskHistoryRepository : CoroutineCrudRepository<History<Task, TaskId>, String>