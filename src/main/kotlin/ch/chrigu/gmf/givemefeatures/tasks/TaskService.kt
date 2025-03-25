package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

// TODO: UI should provide version upon which the change is for. So we could throw error if change is not mergeable.
@Service
class TaskService(private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider) {
    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun updateTask(id: TaskId, update: Task.TaskUpdate) = update(id) { this.update(update) }
    suspend fun blockTask(id: TaskId) = update(id) { block() }
    suspend fun reopenTask(id: TaskId) = update(id) { reopen() }
    suspend fun closeTask(id: TaskId) = update(id) { close() }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id.toString()) ?: throw TaskNotFoundException(id)
    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)

    /**
     * Update task without optimistic locking. Instead, get newest version first and apply changes on this.
     */
    private suspend fun update(id: TaskId, apply: Task.() -> Task): Task {
        val task = getTask(id)
        val updatedTask = task.apply()
        return try {
            taskRepository.save(updatedTask)
        } catch (e: OptimisticLockingFailureException) {
            mergeTask(updatedTask)
        }
    }

    private suspend fun mergeTask(task: Task): Task {
        val merged = getTask(task.id!!).mergeWith(task)
        return taskRepository.save(merged)
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
