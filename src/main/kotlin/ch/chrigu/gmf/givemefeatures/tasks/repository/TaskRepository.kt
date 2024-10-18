package ch.chrigu.gmf.givemefeatures.tasks.repository

import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface TaskRepository : CoroutineCrudRepository<Task, TaskId>