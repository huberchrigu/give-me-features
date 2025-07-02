package ch.chrigu.gmf.givemefeatures.features.domain

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.repository.FeatureRepository
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.shared.markdown.MarkdownDiff
import org.springframework.stereotype.Component

@Component
class FeatureDomainService(private val featureRepository: FeatureRepository) {
    suspend fun mergeWithVersion(id: FeatureId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Feature {
        val theirs = featureRepository.findVersion(id, mergeWithVersion)!!
        val base = featureRepository.findVersion(id, baseVersion)!!
        val mergedName = MarkdownDiff.simpleMerge(base.name, newName, theirs.name)
        val mergedDescription = MarkdownDiff.merge3(
            base.description,
            newDescription,
            theirs.description
        )
        return Feature(id, mergedName, mergedDescription, theirs.tasks, theirs.version)
    }
}
