package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.tasks.Task
import java.util.UUID

data class Feature(val id: UUID?, val name: String, val description: String, val tasks: List<Task>) {
    companion object {
        fun describeNewFeature(name: String, description: String) = Feature(null, name, description, emptyList())
    }
}
