package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider) {
    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun update(id: TaskId, update: Task.TaskUpdate) = getTask(id)
        .update(update)
        .let { taskRepository.save(it) }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id.toString()) ?: throw TaskNotFoundException(id)
    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)
}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String
)
