package ch.chrigu.gmf.givemefeatures.tasks.domain

import ch.chrigu.gmf.givemefeatures.shared.history.HistoryMerger
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
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