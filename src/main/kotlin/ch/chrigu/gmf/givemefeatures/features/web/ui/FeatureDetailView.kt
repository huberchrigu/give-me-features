package ch.chrigu.gmf.givemefeatures.features.web.ui

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureId
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import kotlinx.coroutines.flow.toList

class FeatureDetailView(val id: FeatureId, val name: String, val description: String, val tasks: List<Task>, val version: Long)

suspend fun Feature.asDetailView(taskService: TaskService) = FeatureDetailView(id, name, description.toHtml(), taskService.resolve(tasks).toList(), version!!)
