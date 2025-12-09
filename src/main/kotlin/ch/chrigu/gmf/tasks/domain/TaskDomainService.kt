package ch.chrigu.gmf.tasks.domain

import ch.chrigu.gmf.shared.history.HistoryMerger
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.repository.TaskRepository
import org.springframework.stereotype.Component

@Component
class TaskDomainService(taskRepository: TaskRepository) {
    private val historyMerger = HistoryMerger(taskRepository)

    suspend fun mergeWithVersion(id: TaskId, newName: String, newDescription: Markdown, baseVersion: Long, mergeWithVersion: Long): Task {
        return historyMerger.mergeWithVersion(id, baseVersion, mergeWithVersion) {
            Task(id, theirs.version, newName.merge { name }, newDescription.merge { description }, theirs.status)
        }
    }
}