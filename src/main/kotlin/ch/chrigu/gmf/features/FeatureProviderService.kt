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
        return modify(from, version, to) { linkTask(to) }
    }

    override suspend fun unlink(item: String, from: Task, version: Long): Flow<TaskLinkedItem<*>> {
        return modify(item, version, from) { unlinkTask(from) }
    }

    private suspend fun modify(id: String, version: Long, task: Task, modifier: Feature.() -> Feature): Flow<TaskLinkedItem<FeatureId>> {
        val featureId = FeatureId(id)
        featureRepository.applyOn(featureId, version) {
            this.modifier()
        } ?: throw FeatureNotFoundException(featureId)
        return getFor(task.id)
    }

    private fun Flow<Feature>.toLinkedItems() = map { TaskLinkedItem(it.id, it.name, it.version!!) }
}
