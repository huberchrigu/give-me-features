package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider) {
    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks.map { it.toString() })
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun updateTask(id: TaskId, oldVersion: Int, update: Task.TaskUpdate) = update(id, oldVersion) { this.update(update) }
    suspend fun blockTask(id: TaskId, oldVersion: Int) = update(id, oldVersion) { block() }
    suspend fun reopenTask(id: TaskId, oldVersion: Int) = update(id, oldVersion) { reopen() }
    suspend fun closeTask(id: TaskId, oldVersion: Int) = update(id, oldVersion) { close() }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id.toString()) ?: throw TaskNotFoundException(id)
    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)

    private suspend fun update(id: TaskId, updateBasedOnVersion: Int, apply: Task.() -> Task) = getTask(id)
        .mergeWith(updateBasedOnVersion, apply) { taskRepository.getTaskByIdAndVersion(id, it) } // TODO: Does not make sense, old version and data do not match
        .let { taskRepository.save(it) }
}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String
)
