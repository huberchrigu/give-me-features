package ch.chrigu.gmf.features

import java.util.UUID

data class FeatureId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString(): String = id
}