package ch.chrigu.gmf.givemefeatures.tasks.repository.mongo

import ch.chrigu.gmf.givemefeatures.tasks.Task
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineTaskRepository : CoroutineCrudRepository<Task, String>