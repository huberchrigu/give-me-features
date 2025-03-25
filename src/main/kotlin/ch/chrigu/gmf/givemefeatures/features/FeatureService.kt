package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.tasks.*
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository, private val taskService: TaskService) {
    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()

    suspend fun addTask(id: FeatureId, task: Task): Feature? {
        val updatedFeature = featureRepository.findById(id.toString())?.planNewTask(taskService.newTask(task)) ?: return null
        return try {
            featureRepository.save(updatedFeature)
        } catch (e: OptimisticLockingFailureException) {
            mergeFeature(updatedFeature)
        }
    }

    private suspend fun mergeFeature(feature: Feature): Feature {
        val merged = featureRepository.findById(feature.id!!.toString())!!.mergeWith(feature)
        return featureRepository.save(merged)
    }

    suspend fun getFeature(id: FeatureId) = featureRepository.findById(id.toString())
}
