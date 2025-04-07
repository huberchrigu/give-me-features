package ch.chrigu.gmf.givemefeatures.features.repository

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.shared.history.HistoryRepository

interface FeatureRepository : HistoryRepository<Feature, FeatureId>