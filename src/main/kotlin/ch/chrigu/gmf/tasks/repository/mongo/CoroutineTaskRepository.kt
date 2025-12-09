package ch.chrigu.gmf.tasks.repository.mongo

import ch.chrigu.gmf.tasks.Task
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineTaskRepository : CoroutineCrudRepository<Task, String>