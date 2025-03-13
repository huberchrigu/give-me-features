package ch.chrigu.gmf.givemefeatures.tasks.repository

import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * value class ids do not work yet.
 */
interface TaskRepository : CoroutineCrudRepository<Task, String> {
    fun getTaskByIdAndVersion(id: TaskId, version: Int): Task
}
