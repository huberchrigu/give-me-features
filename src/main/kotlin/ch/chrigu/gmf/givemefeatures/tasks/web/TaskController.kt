package ch.chrigu.gmf.givemefeatures.tasks.web

import ch.chrigu.gmf.givemefeatures.features.web.Hx
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.TaskService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) { // TODO: UI Test (incl. error handling), link to features
    @GetMapping("/{taskId}")
    suspend fun getTask(@PathVariable taskId: TaskId) = Rendering.view("task")
        .modelAttribute("task", taskService.getTask(taskId))
        .build()

    @GetMapping("/{taskId}/edit")
    suspend fun getTaskEditForm(@PathVariable taskId: TaskId) = Rendering.view("task-edit")
        .modelAttribute("task", taskService.getTask(taskId))
        .build()

    @GetMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun getTaskSnippet(@PathVariable taskId: TaskId) = Rendering.view("task :: task")
        .modelAttribute("task", taskService.getTask(taskId))
        .build()

    @PatchMapping("/{taskId}", headers = [Hx.HEADER])
    suspend fun updateTask(@PathVariable taskId: TaskId, @Valid updateTask: UpdateTaskDto) = Rendering.view("task :: task")
        .modelAttribute("task", taskService.update(taskId, updateTask.toChange()))
        .build()

    data class UpdateTaskDto(@field:NotEmpty val name: String?, @field:NotNull val description: String?) {
        fun toChange() = Task.TaskUpdate(name!!, description!!)
    }

}
