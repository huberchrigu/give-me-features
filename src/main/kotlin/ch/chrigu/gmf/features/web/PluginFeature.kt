package ch.chrigu.gmf.features.web

import ch.chrigu.gmf.plugins.FeatureReference
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot

class PluginFeature(id: FeatureId, version: Long?) : FeatureReference, AbstractAggregateRoot<FeatureReferenceId>(id, version)
