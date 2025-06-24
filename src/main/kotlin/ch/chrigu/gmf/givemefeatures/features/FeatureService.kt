package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.shared.markdown.MarkdownDiff
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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

    suspend fun getDescriptionUpdates(id: FeatureId, version: Long): Flow<Feature> {
        val feature = featureRepository.findVersion(id, version)
        return changes.listen(id).filter { it.description != feature?.description }
    }

    suspend fun mergeDescription(id: FeatureId, newDescription: Markdown, baseVersion: Long, compareWith: Long): Markdown {
        return MarkdownDiff.merge3(
            featureRepository.findVersion(id, baseVersion)!!.description,
            newDescription,
            featureRepository.findVersion(id, compareWith)?.description
        )
    }

    private suspend fun update(
        id: FeatureId,
        version: Long,
        applyChange: suspend Feature.() -> Feature
    ): Feature = featureRepository.applyOn(id, version, applyChange)
        ?: throw FeatureNotFoundException(id)
}

class FeatureNotFoundException(id: FeatureId) : AggregateNotFoundException("Feature $id not found")

