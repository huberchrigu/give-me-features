package ch.chrigu.gmf.features.repository.mongo

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.tasks.TaskId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineFeatureRepository : CoroutineCrudRepository<Feature, String> {
    fun findByTasksContains(taskId: List<TaskId>): Flow<Feature>
    fun findByNameContainsIgnoreCaseAndTasksNotContains(name: String, taskId: List<TaskId>): Flow<Feature>
}
