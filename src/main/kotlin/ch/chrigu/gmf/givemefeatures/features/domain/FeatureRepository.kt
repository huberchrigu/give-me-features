package ch.chrigu.gmf.givemefeatures.features.domain

import ch.chrigu.gmf.givemefeatures.features.Feature
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface FeatureRepository : CoroutineCrudRepository<Feature, UUID>