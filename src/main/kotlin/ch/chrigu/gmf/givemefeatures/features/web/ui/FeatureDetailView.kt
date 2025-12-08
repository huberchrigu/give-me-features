package ch.chrigu.gmf.givemefeatures.features.web.ui

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus
import kotlinx.coroutines.flow.toList

class FeatureDetailView(val id: FeatureId, val name: String, val description: String, val tasks: List<Task>, val version: Long, val progress: Int)

suspend fun Feature.asDetailView(taskService: TaskService): FeatureDetailView {
    val tasks = taskService.resolve(tasks).toList()
    return FeatureDetailView(id, name, description.toHtml(), tasks, version!!, tasks.toProgress())
}

private fun List<Task>.toProgress() = 100 * count { it.status == TaskStatus.DONE } / size
