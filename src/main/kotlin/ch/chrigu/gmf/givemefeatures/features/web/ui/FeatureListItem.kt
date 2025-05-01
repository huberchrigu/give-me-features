package ch.chrigu.gmf.givemefeatures.features.web.ui

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId

class FeatureListItem(val id: FeatureId, val name: String, val link: String)

fun Feature.asListItem() = FeatureListItem(id, name, "/features/${id}")
