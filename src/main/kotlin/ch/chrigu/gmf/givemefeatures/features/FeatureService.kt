package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.tasks.*
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository, private val taskService: TaskService) : LinkedItemProvider {
    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()

    suspend fun addTask(id: FeatureId, task: Task) = featureRepository.findById(id.toString())
        ?.planNewTask(taskService.newTask(task))
        ?.let { featureRepository.save(it) }

    suspend fun getFeature(id: FeatureId) = featureRepository.findById(id.toString())

    override fun getFor(taskId: TaskId) = featureRepository.findByTasksContains(listOf(taskId))
        .map { TaskLinkedItem(it.id, it.name) }
}
