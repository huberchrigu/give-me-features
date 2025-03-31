package ch.chrigu.gmf.givemefeatures.features.repository.mongo

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineFeatureRepository : CoroutineCrudRepository<Feature, String> {
    fun findByTasksContains(taskId: List<TaskId>): Flow<Feature>
}
