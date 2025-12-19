package ch.chrigu.gmf.tasks

import ch.chrigu.gmf.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.tasks.domain.TaskDomainService
import ch.chrigu.gmf.tasks.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository, private val linkedItemProvider: LinkedItemProvider, aggregateChangesFactory: AggregateChangesFactory,
    private val taskDomainService: TaskDomainService
) {
    private val changes = aggregateChangesFactory.create(Task::class.java)

    fun resolve(tasks: List<TaskId>) = taskRepository.findAllById(tasks)
    suspend fun newTask(task: Task) = taskRepository.save(task)
    suspend fun updateTask(id: TaskId, version: Long, update: Task.TaskUpdate) = update(id, version) { this.update(update) }
    suspend fun blockTask(id: TaskId, version: Long) = update(id, version) { block() }
    suspend fun reopenTask(id: TaskId, version: Long) = update(id, version) { reopen() }
    suspend fun closeTask(id: TaskId, version: Long) = update(id, version) { close() }

    suspend fun getTask(id: TaskId) = taskRepository.findById(id) ?: throw TaskNotFoundException(id)

    fun getTaskUpdates(id: TaskId): Flow<Task> = changes.listen(id)

    /**
     * @return Old version to new version
     */
    suspend fun getUpdatesWithChangedValues(id: TaskId, version: Long): Flow<Pair<Task, Task>> {
        val task = taskRepository.findVersion(id, version)!!
        return changes.listen(id).filter { it.description != task.description || it.name != task.name }
            .map { task to it }
    }

    fun getLinkedItems(taskId: TaskId) = linkedItemProvider.getFor(taskId)

    fun getLinkableItems(taskId: TaskId, name: String) = linkedItemProvider.search(name, taskId)

    suspend fun linkTo(taskId: TaskId, item: String, version: Long) = linkedItemProvider.link(item, getTask(taskId), version)
    suspend fun unlink(taskId: TaskId, item: String, version: Long) = linkedItemProvider.unlink(item, getTask(taskId), version)

    private suspend fun update(id: TaskId, version: Long, applyChange: Task.() -> Task) = taskRepository.applyOn(id, version, applyChange)
        ?: throw TaskNotFoundException(id)

    suspend fun mergeWithVersion(id: TaskId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Task {
        return taskDomainService.mergeWithVersion(id, newName, newDescription, baseVersion, mergeWithVersion)
    }

}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>

    /**
     * Get all items that contain the [name] param in their name, but are not yet assigned to [taskId].
     */
    fun search(name: String, taskId: TaskId): Flow<TaskLinkedItem<*>>

    /**
     * @param version The linked item's version.
     */
    suspend fun link(from: String, to: Task, version: Long): Flow<TaskLinkedItem<*>>
    suspend fun unlink(item: String, from: Task, version: Long): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String,
    val version: Long
)
