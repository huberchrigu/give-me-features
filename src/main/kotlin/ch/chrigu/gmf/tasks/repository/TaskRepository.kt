package ch.chrigu.gmf.tasks.repository

import ch.chrigu.gmf.shared.history.HistoryRepository
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId

/**
 * value class ids do not work yet.
 */
interface TaskRepository : HistoryRepository<Task, TaskId>
