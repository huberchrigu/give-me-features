package ch.chrigu.gmf.features.repository.mongo

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.features.history.FeatureMerger
import ch.chrigu.gmf.features.repository.FeatureRepository
import ch.chrigu.gmf.shared.history.AbstractHistoryRepository
import ch.chrigu.gmf.shared.history.DocumentHistoryRepository
import ch.chrigu.gmf.tasks.TaskId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator

@Repository
class FeatureHistoryRepository(
    private val featureRepository: CoroutineFeatureRepository,
    mongoTemplate: ReactiveMongoTemplate,
    transactionalOperator: TransactionalOperator
) : FeatureRepository, AbstractHistoryRepository<Feature, FeatureId>(
    featureRepository,
    DocumentHistoryRepository(mongoTemplate, "featureHistory"),
    FeatureMerger(),
    transactionalOperator
) {
    override fun findByTasksContains(taskId: List<TaskId>) = featureRepository.findByTasksContains(taskId)

    override fun findByNameContainsAndTasksNotContains(name: String, taskId: List<TaskId>) = featureRepository.findByNameContainsAndTasksNotContains(name, taskId)
}
