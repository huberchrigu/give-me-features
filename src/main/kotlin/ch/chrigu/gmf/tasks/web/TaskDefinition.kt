package ch.chrigu.gmf.tasks.web

import ch.chrigu.gmf.plugins.ItemDefinition
import ch.chrigu.gmf.plugins.Plugin
import ch.chrigu.gmf.plugins.TaskReference
import ch.chrigu.gmf.plugins.ParentDefinition
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskService
import org.springframework.stereotype.Component

@Component
class TaskDefinition(private val taskService: TaskService) : ParentDefinition<TaskReference> {
    override val uriPrefix: String = "/tasks"

    override fun getItemDefinition(plugin: Plugin): ItemDefinition<TaskReference, *>? {
        return plugin.touchpoints.taskItem
    }

    override suspend fun resolve(id: String): TaskReference {
        return taskService.getTask(TaskId(id)).asReference()
    }
}

fun Task.asReference() = PluginTask(id, version)