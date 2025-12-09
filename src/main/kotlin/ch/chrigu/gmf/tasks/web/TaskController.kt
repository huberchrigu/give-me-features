package ch.chrigu.gmf.tasks.web

import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.web.Hx
import ch.chrigu.gmf.shared.web.UpdateFragmentBuilder
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.Task.TaskUpdate
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.TaskLinkedItem
import ch.chrigu.gmf.tasks.TaskService
import ch.chrigu.gmf.tasks.TaskStatus
import ch.chrigu.gmf.tasks.web.ui.TaskDetails
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.view.Fragment
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/tasks")
@Suppress("SpringMVCViewInspection")
class TaskController(private val taskService: TaskService) {
    private val updateFragmentBuilder = UpdateFragmentBuilder<Task>(
        "tasks",
        "name" to { name },
        "description" to { description })

    @GetMapping("/{taskId}")
    suspend fun getTask(@PathVariable taskId: TaskId) = Rendering.view("task")
        .modelAttribute("task", taskService.getTask(taskId).toDetails())
        .modelAttribute("items", taskService.getLinkedItems(taskId).toList())
        .build()

    @GetMapping("/{taskId}/edit", headers = [Hx.HEADER])
    suspend fun getTaskEditForm(@PathVariable taskId: TaskId) = taskEditView(taskService.getTask(taskId))

    @GetMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun getTaskSnippet(@PathVariable taskId: TaskId) = Rendering.view("blocks/task")
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

    @PutMapping("/{id}${UpdateFragmentBuilder.MERGE_URI}", headers = [Hx.HEADER])
    suspend fun mergeTask(@PathVariable id: TaskId, @Valid mergeTaskBody: MergeTaskBody, @RequestParam version: Long) = taskEditView(
        taskService.mergeWithVersion(id, mergeTaskBody.name!!, mergeTaskBody.description!!, version, mergeTaskBody.newVersion!!)
    )

    @PatchMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun updateTask(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTask: UpdateTaskBody) = Rendering.view("blocks/task")
        .modelAttribute("task", taskService.updateTask(taskId, version, updateTask.toChange()).toDetails())
        .build()

    @PutMapping("/{taskId}/status", headers = [Hx.HEADER])
    suspend fun updateStatus(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTaskStatus: UpdateTaskStatus) = Rendering.view("blocks/task")
        .modelAttribute("task", updateTaskStatus.applyOn(taskService, taskId, version).toDetails())
        .build()

    @GetMapping("/{taskId}/link-feature", headers = [Hx.HEADER])
    fun getLinkFeatureForm(@PathVariable taskId: TaskId) = Rendering.view("blocks/task-link-feature")
        .modelAttribute("taskId", taskId)
        .build()

    @GetMapping("/{taskId}/link-feature/cancel", headers = [Hx.HEADER])
    fun cancelLinkFeature(@PathVariable taskId: TaskId) = linkedFeaturesBlock(taskId, taskService.getLinkedItems(taskId))

    @GetMapping("/{taskId}/link-feature/search", headers = [Hx.HEADER])
    fun searchLinkableFeatures(@PathVariable taskId: TaskId, @RequestParam name: String) = Rendering.view("blocks/task-link-feature-search")
        .modelAttribute("taskId", taskId)
        .modelAttribute("items", taskService.getLinkableItems(taskId, name))
        .build()

    @PutMapping("/{taskId}/link-feature", headers = [Hx.HEADER])
    suspend fun linkFeature(@PathVariable taskId: TaskId, @Valid linkBody: LinkBody): Rendering {
        val items = taskService.linkTo(taskId, linkBody.item!!, linkBody.version!!)
        return linkedFeaturesBlock(taskId, items)
    }

    private fun linkedFeaturesBlock(
        taskId: TaskId,
        items: Flow<TaskLinkedItem<*>>
    ): Rendering = Rendering.view("blocks/linked-features")
        .modelAttribute("taskId", taskId)
        .modelAttribute("items", items)
        .build()

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

    private fun Task.toDetails() = TaskDetails(id, name, description.toHtml(), status, getAvailableStatus(), version!!)
}
