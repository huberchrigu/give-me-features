package ch.chrigu.gmf.givemefeatures.tasks.repository

import ch.chrigu.gmf.givemefeatures.shared.history.HistoryRepository
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

/**
 * value class ids do not work yet.
 */
interface TaskRepository : HistoryRepository<Task, TaskId>
