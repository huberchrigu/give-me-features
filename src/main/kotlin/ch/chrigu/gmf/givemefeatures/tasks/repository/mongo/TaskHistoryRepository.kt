package ch.chrigu.gmf.givemefeatures.tasks.repository.mongo

import ch.chrigu.gmf.givemefeatures.shared.history.AbstractHistoryRepository
import ch.chrigu.gmf.givemefeatures.shared.history.History
import ch.chrigu.gmf.givemefeatures.tasks.Task
import ch.chrigu.gmf.givemefeatures.tasks.TaskId
import ch.chrigu.gmf.givemefeatures.tasks.merger.TaskMerger
import ch.chrigu.gmf.givemefeatures.tasks.repository.TaskRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator

@Repository
class TaskHistoryRepository(
    taskRepository: CoroutineCrudRepository<Task, String>,
    taskHistoryRepository: CoroutineCrudRepository<History<Task, TaskId>, String>,
    transactionalOperator: TransactionalOperator
) : TaskRepository, AbstractHistoryRepository<Task, TaskId>(
    taskRepository,
    taskHistoryRepository,
    TaskMerger(),
    transactionalOperator
)
