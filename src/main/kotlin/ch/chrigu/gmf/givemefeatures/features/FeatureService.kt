package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.shared.history.UpdateService
import ch.chrigu.gmf.givemefeatures.tasks.*
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository, private val taskService: TaskService) {
    private val updateService = UpdateService<FeatureId, Feature>({ getFeature(it) }) { featureRepository.save(it) }

    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()

    suspend fun addTask(id: FeatureId, version: Long, task: Task): Feature? {
        return updateService.update(id, version) { planNewTask(taskService.newTask(task)) }
    }

    suspend fun getFeature(id: FeatureId) = featureRepository.findById(id.toString()) ?: throw FeatureNotFoundException(id)
}

class FeatureNotFoundException(id: FeatureId) : AggregateNotFoundException("Feature $id not found")
