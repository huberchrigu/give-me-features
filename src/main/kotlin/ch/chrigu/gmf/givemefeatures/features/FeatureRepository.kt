package ch.chrigu.gmf.givemefeatures.features

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface FeatureRepository : CoroutineCrudRepository<Feature, UUID>