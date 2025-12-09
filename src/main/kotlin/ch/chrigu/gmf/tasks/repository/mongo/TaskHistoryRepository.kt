package ch.chrigu.gmf.tasks.repository.mongo

import ch.chrigu.gmf.shared.history.AbstractHistoryRepository
import ch.chrigu.gmf.shared.history.DocumentHistoryRepository
import ch.chrigu.gmf.tasks.Task
import ch.chrigu.gmf.tasks.TaskId
import ch.chrigu.gmf.tasks.merger.TaskMerger
import ch.chrigu.gmf.tasks.repository.TaskRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator

@Repository
class TaskHistoryRepository(
    taskRepository: CoroutineCrudRepository<Task, String>,
    mongoTemplate: ReactiveMongoTemplate,
    transactionalOperator: TransactionalOperator
) : TaskRepository, AbstractHistoryRepository<Task, TaskId>(
    taskRepository,
    DocumentHistoryRepository(mongoTemplate, "taskHistory"),
    TaskMerger(),
    transactionalOperator
)
