package ch.chrigu.gmf.features

import ch.chrigu.gmf.features.domain.FeatureDomainService
import ch.chrigu.gmf.features.repository.FeatureRepository
import ch.chrigu.gmf.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class FeatureService(
    private val featureRepository: FeatureRepository, private val taskService: TaskService, aggregateChangesFactory: AggregateChangesFactory,
    private val featureDomainService: FeatureDomainService
) {
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

    /**
     * @return Old version to new version
     */
    suspend fun getUpdatesWithChangedValues(id: FeatureId, version: Long): Flow<Pair<Feature, Feature>> {
        val feature = featureRepository.findVersion(id, version)!!
        return changes.listen(id).filter { it.description != feature.description || it.name != feature.name }
            .map { feature to it }
    }

    suspend fun mergeWithVersion(id: FeatureId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Feature {
        return featureDomainService.mergeWithVersion(id, newName, newDescription, baseVersion, mergeWithVersion)
    }

    private suspend fun update(
        id: FeatureId,
        version: Long,
        applyChange: suspend Feature.() -> Feature
    ): Feature = featureRepository.applyOn(id, version, applyChange) ?: throw FeatureNotFoundException(id)
}

class FeatureNotFoundException(id: FeatureId) : AggregateNotFoundException("Feature $id not found")
