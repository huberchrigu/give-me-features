package ch.chrigu.gmf.features.web

import ch.chrigu.gfm.plugin.FeatureReference
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot

class PluginFeature(id: FeatureId, version: Long?) : FeatureReference, AbstractAggregateRoot<FeatureId>(id, version)
