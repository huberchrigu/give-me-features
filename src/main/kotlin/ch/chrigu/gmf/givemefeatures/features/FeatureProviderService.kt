package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.tasks.LinkedItemProvider
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskLinkedItem
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class FeatureProviderService(private val featureRepository: FeatureRepository) : LinkedItemProvider {
    override fun getFor(taskId: TaskId) = featureRepository.findByTasksContains(listOf(taskId))
        .map { TaskLinkedItem(it.id, it.name) }
}
