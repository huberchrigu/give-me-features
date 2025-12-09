package ch.chrigu.gmf.features.web.ui

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId

class FeatureListItem(val id: FeatureId, val name: String, val link: String)

fun Feature.asListItem() = FeatureListItem(id, name, "/features/${id}")
