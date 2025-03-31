package ch.chrigu.gmf.givemefeatures.tasks.repository.mongo

import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineTaskHistoryRepository : CoroutineCrudRepository<History<Task, TaskId>, String>