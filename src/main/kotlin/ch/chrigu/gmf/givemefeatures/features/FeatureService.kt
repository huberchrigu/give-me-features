package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository, private val taskService: TaskService, aggregateChangesFactory: AggregateChangesFactory) {
    private val changes = aggregateChangesFactory.create(Feature::class.java)

    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()

    suspend fun addTask(id: FeatureId, version: Long, task: Task): Feature? {
        return update(id, version) { planNewTask(taskService.newTask(task)) }
    }

    suspend fun getFeature(id: FeatureId) = featureRepository.findById(id) ?: throw FeatureNotFoundException(id)

    suspend fun updateFeature(id: FeatureId, version: Long, featureUpdate: FeatureUpdate): Feature {
        return update(id, version) { update(featureUpdate) }
    }

    fun getUpdates(id: FeatureId) = changes.listen(id)
    fun getAllUpdates() = changes.listenToAll()

    private suspend fun update(
        id: FeatureId,
        version: Long,
        applyChange: suspend Feature.() -> Feature
    ): Feature = featureRepository.applyOn(id, version, applyChange)
        ?.apply { changes.emitIfListened(this.id, this) }
        ?: throw FeatureNotFoundException(id)
}

class FeatureNotFoundException(id: FeatureId) : AggregateNotFoundException("Feature $id not found")
