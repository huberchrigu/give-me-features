package ch.chrigu.gmf.givemefeatures.features.repository.mongo

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.shared.history.History
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CoroutineFeatureHistoryRepository : CoroutineCrudRepository<History<Feature, FeatureId>, String>