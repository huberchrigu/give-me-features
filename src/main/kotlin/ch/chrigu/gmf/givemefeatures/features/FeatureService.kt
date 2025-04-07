package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository, private val taskService: TaskService) {
    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()

    suspend fun addTask(id: FeatureId, version: Long, task: Task): Feature? {
        return featureRepository.applyOn(id, version) { planNewTask(taskService.newTask(task)) } ?: throw FeatureNotFoundException(id)
    }

    suspend fun getFeature(id: FeatureId) = featureRepository.findById(id) ?: throw FeatureNotFoundException(id)
    suspend fun updateFeature(id: FeatureId, version: Long, featureUpdate: FeatureUpdate): Feature {
        return featureRepository.applyOn(id, version) { update(featureUpdate) } ?: throw FeatureNotFoundException(id)
    }
}

class FeatureNotFoundException(id: FeatureId) : AggregateNotFoundException("Feature $id not found")