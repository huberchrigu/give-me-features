package ch.chrigu.gmf.givemefeatures.features.web.ui

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId

class FeatureListItem(val name: String, val link: String, val current: Boolean)

fun Feature.asListItem(current: FeatureId?) = FeatureListItem(name, "/features/${id!!}", current == id)
