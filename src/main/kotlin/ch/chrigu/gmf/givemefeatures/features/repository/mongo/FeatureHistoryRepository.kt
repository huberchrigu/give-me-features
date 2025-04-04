package ch.chrigu.gmf.givemefeatures.features.repository.mongo

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.history.FeatureMerger
import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractHistoryRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator

@Repository
class FeatureHistoryRepository(
    featureRepository: CoroutineFeatureRepository,
    historyRepository: CoroutineFeatureHistoryRepository,
    transactionalOperator: TransactionalOperator
) : FeatureRepository, AbstractHistoryRepository<Feature, FeatureId>(
    featureRepository,
    historyRepository,
    FeatureMerger(),
    transactionalOperator
)
