package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import ch.chrigu.gmf.givemefeatures.shared.Html
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import ch.chrigu.gmf.givemefeatures.tasks.TaskStatus
import ch.chrigu.gmf.givemefeatures.tasks.web.ui.TaskDetails
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {
    @GetMapping("/{taskId}")
    suspend fun getTask(@PathVariable taskId: TaskId) = Rendering.view("task")
        .modelAttribute("task", taskService.getTask(taskId).toDetails())
        .modelAttribute("items", taskService.getLinkedItems(taskId).toList())
        .build()

    @GetMapping("/{taskId}/edit", headers = [Hx.HEADER])
    suspend fun getTaskEditForm(@PathVariable taskId: TaskId) = Rendering.view("blocks/task-edit")
        .modelAttribute("task", taskService.getTask(taskId))
        .build()

    @Suppress("SpringMVCViewInspection")
    @GetMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun getTaskSnippet(@PathVariable taskId: TaskId) = Rendering.view("blocks/task")
        .modelAttribute("task", taskService.getTask(taskId).toDetails())
        .build()

    @Suppress("SpringMVCViewInspection")
    @PatchMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun updateTask(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTask: UpdateTaskDto) = Rendering.view("blocks/task")
        .modelAttribute("task", taskService.updateTask(taskId, version, updateTask.toChange()).toDetails())
        .build()

    @PutMapping("/{taskId}/status", headers = [Hx.HEADER])
    suspend fun updateStatus(@PathVariable taskId: TaskId, @RequestParam version: Long, @Valid updateTaskStatus: UpdateTaskStatus) = Rendering.view("blocks/task")
        .modelAttribute("task", updateTaskStatus.applyOn(taskService, taskId, version).toDetails())
        .build()

    data class UpdateTaskStatus(@field:NotNull val status: TaskStatus?) {
        suspend fun applyOn(taskService: TaskService, id: TaskId, version: Long) = when (status!!) {
            TaskStatus.BLOCKED -> taskService.blockTask(id, version)
            TaskStatus.DONE -> taskService.closeTask(id, version)
            TaskStatus.OPEN -> taskService.reopenTask(id, version)
        }
    }

    data class UpdateTaskDto(@field:NotEmpty val name: String?, @field:NotNull val description: String?) {
        fun toChange() = Task.TaskUpdate(name!!, Html(description!!))
    }

    private fun Task.toDetails() = TaskDetails(id!!.toString(), name, description.toString(), status, getAvailableStatus(), version!!)
}
