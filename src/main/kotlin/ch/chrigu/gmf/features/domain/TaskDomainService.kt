package ch.chrigu.gmf.features.domain

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.features.repository.FeatureRepository
import ch.chrigu.gmf.shared.history.HistoryMerger
import ch.chrigu.gmf.shared.markdown.Markdown
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
