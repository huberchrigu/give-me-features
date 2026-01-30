package ch.chrigu.gmf.tasks.web

import ch.chrigu.gfm.plugin.ItemDefinition
import ch.chrigu.gfm.plugin.Plugin
import ch.chrigu.gfm.plugin.TaskReference
import ch.chrigu.gmf.plugins.PluginService
import ch.chrigu.gmf.plugins.web.PluginFormController
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.web.Hx
import ch.chrigu.gmf.shared.web.UpdateFragmentBuilder
import ch.chrigu.gmf.tasks.*
import ch.chrigu.gmf.tasks.Task.TaskUpdate
import ch.chrigu.gmf.tasks.web.ui.toDetails
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.view.Fragment
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/tasks")
@Suppress("SpringMVCViewInspection")
class TaskController(private val taskService: TaskService, override val pluginService: PluginService) : PluginFormController<TaskReference, TaskId> {
    private val updateFragmentBuilder = UpdateFragmentBuilder<Task>(
        "tasks",
        "name" to { name },
        "description" to { description })

    @GetMapping("/{taskId}")
    suspend fun getTask(@PathVariable taskId: TaskId): Rendering = Rendering.view("task")
        .modelAttribute("task", taskService.getTask(taskId).toDetails().withPlugins { getPluginsFor(it) })
        .modelAttribute("items", taskService.getLinkedItems(taskId).toList())
        .build()

    @GetMapping("/{taskId}/edit", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun getTaskEditForm(@PathVariable taskId: TaskId) = taskEditView(taskService.getTask(taskId))

    @GetMapping("/{taskId}", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun getTaskSnippet(@PathVariable taskId: TaskId): Rendering = Rendering.view("blocks/task")
        .modelAttribute("task", taskService.getTask(taskId).toDetails())
        .build()

    @GetMapping("/{taskId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getTaskUpdates(@PathVariable taskId: TaskId) = taskService.getTaskUpdates(taskId)
        .map {
            ServerSentEvent.builder(
                Fragment.create("blocks/task", mapOf("task" to it.toDetails()))
            ).build()
        }

    /**
     * Streams fragments of changed values.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @GetMapping("/{id}/fields", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun getTaskFormUpdates(@PathVariable id: TaskId, @RequestParam version: Long) = taskService.getUpdatesWithChangedValues(id, version)
        .flatMapConcat { updateFragmentBuilder.toFragments(it.first, it.second).asFlow() }

    @PutMapping("/{id}${UpdateFragmentBuilder.MERGE_URI}", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun mergeTask(@PathVariable id: TaskId, @Valid mergeTaskBody: MergeTaskBody, @RequestParam version: Long) = taskEditView(
        taskService.mergeWithVersion(id, mergeTaskBody.name!!, mergeTaskBody.description!!, version, mergeTaskBody.newVersion!!)
    )

    @PatchMapping("/{taskId}", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun updateTask(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTask: UpdateTaskBody): Rendering = Rendering.view("blocks/task")
        .modelAttribute("task", taskService.updateTask(taskId, version, updateTask.toChange()).toDetails())
        .build()

    @PutMapping("/{taskId}/status", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun updateStatus(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTaskStatus: UpdateTaskStatus): Rendering = Rendering.view("blocks/task")
        .modelAttribute("task", updateTaskStatus.applyOn(taskService, taskId, version).toDetails())
        .build()

    @GetMapping("/{taskId}/link-feature", headers = [Hx.REQUEST_EQ_TRUE])
    fun getLinkFeatureForm(@PathVariable taskId: TaskId): Rendering = Rendering.view("blocks/task-link-feature")
        .modelAttribute("taskId", taskId)
        .build()

    @GetMapping("/{taskId}/link-feature/cancel", headers = [Hx.REQUEST_EQ_TRUE])
    fun cancelLinkFeature(@PathVariable taskId: TaskId) = linkedFeaturesBlock(taskId, taskService.getLinkedItems(taskId))

    @GetMapping("/{taskId}/link-feature/search", headers = [Hx.REQUEST_EQ_TRUE])
    fun searchLinkableFeatures(@PathVariable taskId: TaskId, @RequestParam name: String): Rendering = Rendering.view("blocks/task-link-feature-search")
        .modelAttribute("taskId", taskId)
        .modelAttribute("items", taskService.getLinkableItems(taskId, name))
        .build()

    @PutMapping("/{taskId}/link-feature", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun linkFeature(@PathVariable taskId: TaskId, @Valid linkBody: LinkBody): Rendering {
        val items = taskService.linkTo(taskId, linkBody.item!!, linkBody.version!!)
        return linkedFeaturesBlock(taskId, items)
    }

    @PostMapping("/{taskId}/unlink-feature", headers = [Hx.REQUEST_EQ_TRUE])
    suspend fun unlinkFeature(@PathVariable taskId: TaskId, @Valid linkBody: LinkBody): Rendering {
        val items = taskService.unlink(taskId, linkBody.item!!, linkBody.version!!)
        return linkedFeaturesBlock(taskId, items)
    }

    override suspend fun resolve(id: TaskId): TaskReference {
        return taskService.getTask(id).asReference()
    }

    override fun getItemDefinition(plugin: Plugin): ItemDefinition<TaskReference, *>? {
        return plugin.touchpoints.taskItem
    }

    private fun linkedFeaturesBlock(taskId: TaskId, items: Flow<TaskLinkedItem<*>>): Rendering = Rendering.view("blocks/linked-features")
        .modelAttribute("taskId", taskId)
        .modelAttribute("items", items)
        .build()

    private fun Task.asReference() = PluginTask()

    private suspend fun taskEditView(task: Task): Rendering = Rendering.view("blocks/task-edit")
        .modelAttribute("task", task)
        .build()

    data class UpdateTaskStatus(@field:NotNull val status: TaskStatus?) {
        suspend fun applyOn(taskService: TaskService, id: TaskId, version: Long) = when (status!!) {
            TaskStatus.BLOCKED -> taskService.blockTask(id, version)
            TaskStatus.DONE -> taskService.closeTask(id, version)
            TaskStatus.OPEN -> taskService.reopenTask(id, version)
        }
    }

    data class UpdateTaskBody(@field:NotEmpty val name: String?, @field:NotNull val description: String?) {
        fun toChange() = TaskUpdate(name!!, Markdown(description!!))
    }

    data class MergeTaskBody(@field:NotEmpty val name: String?, @field:NotNull val description: Markdown?, @field:NotNull val newVersion: Long?)
    data class LinkBody(@field:NotNull val item: String?, @field:NotNull val version: Long?)
}
