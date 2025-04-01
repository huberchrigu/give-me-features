package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.AbstractAggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

class Feature(id: FeatureId?, val name: String, val description: Html, val tasks: List<TaskId>, version: Long?) :
    AbstractAggregateRoot<FeatureId>(id, version) {
    fun planNewTask(task: Task) = Feature(id, name, description, tasks + task.id!!, version)

    companion object {
        fun describeNewFeature(name: String, description: Html) = Feature(null, name, description, emptyList(), null)
    }
}

@JvmInline
value class FeatureId(private val id: String) {
    override fun toString(): String = id
}
