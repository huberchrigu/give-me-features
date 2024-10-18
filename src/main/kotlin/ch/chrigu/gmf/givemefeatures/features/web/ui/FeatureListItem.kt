package ch.chrigu.gmf.givemefeatures.features.web.ui

import ch.chrigu.gmf.givemefeatures.features.Feature

class FeatureListItem(val name: String, val link: String, val current: Boolean)

fun Feature.asListItem(current: Feature?) = FeatureListItem(name, "/features/$id", current == this)