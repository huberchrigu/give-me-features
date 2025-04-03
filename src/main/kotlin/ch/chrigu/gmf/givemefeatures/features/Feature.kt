package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.AbstractAggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import java.util.UUID

class Feature(id: FeatureId, val name: String, val description: Html, val tasks: List<TaskId>, version: Long?) :
    AbstractAggregateRoot<FeatureId>(id, version) {
    fun planNewTask(task: Task) = Feature(id, name, description, tasks + task.id, version)

    companion object {
        fun describeNewFeature(name: String, description: Html) = Feature(FeatureId(), name, description, emptyList(), null)
    }
}

data class FeatureId(private val id: String = UUID.randomUUID().toString()) {
    override fun toString(): String = id
}
