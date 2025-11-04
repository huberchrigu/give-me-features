package ch.chrigu.gmf.givemefeatures.features.domain

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.history.HistoryMerger
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import org.springframework.stereotype.Component

@Component
class FeatureDomainService(featureRepository: FeatureRepository) {
    private val historyMerger = HistoryMerger(featureRepository)

    suspend fun mergeWithVersion(id: FeatureId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Feature {
        return historyMerger.mergeWithVersion(id, baseVersion, mergeWithVersion) {
            Feature(id, newName.merge { name }, newDescription.merge { description }, theirs.tasks, theirs.version)
        }
    }
}
