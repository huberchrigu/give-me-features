package ch.chrigu.gmf.givemefeatures.features.history

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger

class FeatureMerger : AbstractMerger<Feature, FeatureId>() {
    override fun getMergedAggregate(id: FeatureId, version: Long, versions: MergingVersions<Feature>) = with(versions) {
        Feature(id, merge { name }, merge { description }, merge { tasks }, version)
    }
}
