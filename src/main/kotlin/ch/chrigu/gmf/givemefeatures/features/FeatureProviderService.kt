package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.mongo.CoroutineFeatureRepository
import ch.chrigu.gmf.givemefeatures.tasks.LinkedItemProvider
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskLinkedItem
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class FeatureProviderService(private val featureRepository: CoroutineFeatureRepository) : LinkedItemProvider {
    override fun getFor(taskId: TaskId) = featureRepository.findByTasksContains(listOf(taskId))
        .map { TaskLinkedItem(it.id, it.name) }
}
