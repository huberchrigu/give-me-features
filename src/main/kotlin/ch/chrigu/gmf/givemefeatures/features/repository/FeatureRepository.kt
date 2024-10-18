package ch.chrigu.gmf.givemefeatures.features.repository

import ch.chrigu.gmf.givemefeatures.features.Feature
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeatureRepository : CoroutineCrudRepository<Feature, String>