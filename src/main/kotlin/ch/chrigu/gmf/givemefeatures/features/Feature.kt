package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId

data class Feature(val id: FeatureId?, val name: String, val description: Html, val tasks: List<TaskId>) {
    fun planNewTask(task: Task) = copy(tasks = tasks + task.id!!)

    companion object {
        fun describeNewFeature(name: String, description: Html) = Feature(null, name, description, emptyList())
    }
}

@JvmInline
value class FeatureId(private val id: String) {
    override fun toString(): String = id
}
