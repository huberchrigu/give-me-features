package ch.chrigu.gmf.features.web.ui

import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskService
import ch.chrigu.gmf.tasks.TaskStatus
import ch.chrigu.gmf.tasks.web.ui.toDetails
import kotlinx.coroutines.flow.toList

class FeatureDetailView(val id: FeatureId, val name: String, val description: String, tasks: List<Task>, val version: Long, val progress: Int) {
    val tasks = tasks.map { it.toDetails() }
}

suspend fun Feature.asDetailView(taskService: TaskService): FeatureDetailView {
    val tasks = taskService.resolve(tasks).toList()
    return FeatureDetailView(id, name, description.toHtml(), tasks, version!!, tasks.toProgress())
}

private fun List<Task>.toProgress() = if (isEmpty()) 0 else 100 * count { it.status == TaskStatus.DONE } / size
