package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.tasks.Task

data class Feature(val id: FeatureId?, val name: String, val description: String, val tasks: List<Task>) {
    companion object {
        fun describeNewFeature(name: String, description: String) = Feature(null, name, description, emptyList())
    }
}

@JvmInline
value class FeatureId(private val id: String) {
    override fun toString(): String = id
}