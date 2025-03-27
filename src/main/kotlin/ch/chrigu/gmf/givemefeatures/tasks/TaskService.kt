package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.shared.history.UpdateService
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider) {
    private val updateService = UpdateService<TaskId, Task>({ getTask(it) }) { taskRepository.save(it) }

    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun updateTask(id: TaskId, version: Long, update: Task.TaskUpdate) = update(id, version) { this.update(update) }
    suspend fun blockTask(id: TaskId, version: Long) = update(id, version) { block() }
    suspend fun reopenTask(id: TaskId, version: Long) = update(id, version) { reopen() }
    suspend fun closeTask(id: TaskId, version: Long) = update(id, version) { close() }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id.toString()) ?: throw TaskNotFoundException(id)
    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)

    private suspend fun update(id: TaskId, version: Long, applyChange: Task.() -> Task) = updateService.update(id, version, applyChange)
}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String
)
