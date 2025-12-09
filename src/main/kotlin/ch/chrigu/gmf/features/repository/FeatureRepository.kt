package ch.chrigu.gmf.features.repository

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.shared.history.HistoryRepository
import ch.chrigu.gmf.tasks.TaskId
import kotlinx.coroutines.flow.Flow

interface FeatureRepository : HistoryRepository<Feature, FeatureId> {
    fun findByTasksContains(taskId: List<TaskId>): Flow<Feature>
    fun findByNameContainsAndTasksNotContains(name: String, taskId: List<TaskId>): Flow<Feature>
}