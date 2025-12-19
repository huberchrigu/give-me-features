package ch.chrigu.gmf.features

import ch.chrigu.gmf.shared.aggregates.AbstractAggregateRoot
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import java.util.UUID

class Feature(id: FeatureId, val name: String, val description: Markdown, val tasks: List<TaskId>, version: Long?) :
    AbstractAggregateRoot<FeatureId>(id, version) {
    /**
     * Adds a new task underneath this feature.
     */
    fun planNewTask(task: Task) = linkTask(task)

    /**
     * Links an existing task to this feature (tasks may have relationships to multiple features).
     */
    fun linkTask(task: Task): Feature = Feature(id, name, description, tasks + task.id, version)
    fun unlinkTask(task: Task): Feature {
        val taskId = task.id
        require(tasks.contains(taskId)) { "Task $taskId is not linked to feature $id" }
        return Feature(id, name, description, tasks - taskId, version)
    }

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
