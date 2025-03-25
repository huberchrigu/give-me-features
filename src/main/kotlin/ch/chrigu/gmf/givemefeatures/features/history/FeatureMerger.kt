package ch.chrigu.gmf.givemefeatures.features.history

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.features.FeatureSnapshot
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import ch.chrigu.gmf.givemefeatures.shared.history.History

class FeatureMerger(base: Feature, newVersion: Feature, persistedNewVersion: Feature) : AbstractMerger<FeatureSnapshot, Feature, FeatureId>(base, newVersion, persistedNewVersion) {
    override fun mergeWith(id: FeatureId, version: Long, history: History<FeatureSnapshot>) = Feature(id, merge { name }, merge { description }, merge { tasks }, version, history)
}
