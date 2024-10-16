package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.features.domain.FeatureRepository
import org.springframework.stereotype.Service

@Service
class FeatureService(private val featureRepository: FeatureRepository) {
    suspend fun newFeature(feature: Feature) = featureRepository.save(feature)

    fun getFeatures() = featureRepository.findAll()
}
