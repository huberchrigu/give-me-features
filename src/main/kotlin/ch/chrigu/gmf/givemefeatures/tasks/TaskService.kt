package ch.chrigu.gmf.givemefeatures.tasks

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateNotFoundException
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.tasks.domain.TaskDomainService
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
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

    suspend fun mergeWithVersion(id: TaskId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Task {
        return taskDomainService.mergeWithVersion(id, newName, newDescription, baseVersion, mergeWithVersion)
    }

    private suspend fun update(id: TaskId, version: Long, applyChange: Task.() -> Task) = taskRepository.applyOn(id, version, applyChange)
        ?: throw TaskNotFoundException(id)
}

class TaskNotFoundException(id: TaskId) : AggregateNotFoundException("Task $id not found")

interface LinkedItemProvider {
    fun getFor(taskId: TaskId): Flow<TaskLinkedItem<*>>
}

data class TaskLinkedItem<T>(
    val id: T,
    val name: String
)
