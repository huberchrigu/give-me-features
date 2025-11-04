package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.shared.web.Hx
import ch.chrigu.gmf.givemefeatures.shared.web.UpdateFragmentBuilder
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus
import ch.chrigu.gmf.givemefeatures.tasks.web.ui.TaskDetails
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        fun toChange() = Task.TaskUpdate(name!!, Markdown(description!!))
    }

    data class MergeTaskBody(@field:NotEmpty val name: String?, @field:NotNull val description: Markdown?, @field:NotNull val newVersion: Long?)

    private fun Task.toDetails() = TaskDetails(id.toString(), name, description.toHtml(), status, getAvailableStatus(), version!!)
}
