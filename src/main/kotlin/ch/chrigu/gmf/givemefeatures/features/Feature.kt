package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.AbstractAggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.Markdown
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import java.util.UUID

class Feature(id: FeatureId, val name: String, val description: Markdown, val tasks: List<TaskId>, version: Long?) :
    AbstractAggregateRoot<FeatureId>(id, version) {
    fun planNewTask(task: Task) = Feature(id, name, description, tasks + task.id, version)
    fun update(featureUpdate: FeatureUpdate) = featureUpdate.apply(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Feature) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false
        if (description != other.description) return false
        if (tasks != other.tasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + tasks.hashCode()
        return result
    }


    companion object {
        fun describeNewFeature(name: String, description: Markdown) = Feature(FeatureId(), name, description, emptyList(), null)
    }
}

data class FeatureId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString(): String = id
}

data class FeatureUpdate(private val name: String, private val description: Markdown) {
    fun apply(feature: Feature) = Feature(feature.id, name, description, feature.tasks, feature.version)
}