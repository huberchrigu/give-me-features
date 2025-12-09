package ch.chrigu.gmf.features

import ch.chrigu.gmf.features.repository.FeatureRepository
import ch.chrigu.gmf.tasks.LinkedItemProvider
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskLinkedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class FeatureProviderService(private val featureRepository: FeatureRepository) : LinkedItemProvider {
    override fun getFor(taskId: TaskId) = featureRepository.findByTasksContains(listOf(taskId))
        .toLinkedItems()

    override fun search(name: String, taskId: TaskId) = featureRepository.findByNameContainsAndTasksNotContains(name, listOf(taskId))
        .toLinkedItems()

    override suspend fun link(from: String, to: Task, version: Long): Flow<TaskLinkedItem<*>> {
        val featureId = FeatureId(from)
        featureRepository.applyOn(featureId, version) {
                this.linkTask(to)
        } ?: throw FeatureNotFoundException(featureId)
        return getFor(to.id)
    }

    private fun Flow<Feature>.toLinkedItems() = map { TaskLinkedItem(it.id, it.name, it.version!!) }
}
