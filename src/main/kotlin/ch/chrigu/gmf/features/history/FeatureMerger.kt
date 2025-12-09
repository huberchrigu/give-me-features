package ch.chrigu.gmf.features.history

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.shared.history.AbstractMerger

class FeatureMerger : AbstractMerger<Feature, FeatureId>() {
    override fun getMergedAggregate(id: FeatureId, version: Long, versions: MergingVersions<Feature>) = with(versions) {
        Feature(id, merge { name }, merge { description }, merge { tasks }, version)
    }
}
