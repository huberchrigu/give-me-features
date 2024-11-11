package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository) {
    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun update(id: TaskId, update: Task.TaskUpdate) = getTask(id)
        .update(update)
        .let { taskRepository.save(it) }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id.toString()) ?: throw TaskNotFoundException(id)
}

class TaskNotFoundException(id: TaskId) : RuntimeException("Task $id not found") // TODO: Handle in web adapter
