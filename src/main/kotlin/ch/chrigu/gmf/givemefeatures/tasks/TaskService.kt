package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TaskService(private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider) {
    private val taskStates = ConcurrentHashMap<TaskId, MutableSharedFlow<Task>>() // TODO: Make stateless

    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks)
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun updateTask(id: TaskId, version: Long, update: Task.TaskUpdate) = update(id, version) { this.update(update) }
    suspend fun blockTask(id: TaskId, version: Long) = update(id, version) { block() }
    suspend fun reopenTask(id: TaskId, version: Long) = update(id, version) { reopen() }
    suspend fun closeTask(id: TaskId, version: Long) = update(id, version) { close() }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
    fun getTaskUpdates(id: TaskId): Flow<Task> = getTaskState(id).asSharedFlow()

    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)

    private fun getTaskState(id: TaskId) = taskStates.getOrPut(id) {
        MutableSharedFlow()
    }

    private suspend fun update(id: TaskId, version: Long, applyChange: Task.() -> Task) = taskRepository.applyOn(id, version, applyChange)
        ?.apply { registerTaskUpdate(this) }
        ?: throw TaskNotFoundException(id)

    private suspend fun registerTaskUpdate(task: Task) {
        getTaskState(task.id).emit(task)
    }
}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String
)
