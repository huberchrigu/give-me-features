package ch.chrigu.gmf.shared.history

import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.history.merge.HistoryMerger
import ch.chrigu.gmf.shared.mongo.transactional
import kotlinx.coroutines.flow.Flow
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.transaction.reactive.TransactionalOperator

abstract class AbstractHistoryRepository<T : AggregateRoot<ID>, ID : Any>(
    private val aggregateRepository: CoroutineCrudRepository<T, String>,
    private val historyRepository: DocumentHistoryRepository<T, ID>,
    aggregateMerger: AggregateMerger<T, ID>,
    private val transactionalOperator: TransactionalOperator
) : HistoryRepository<T, ID> {
    private val merger = HistoryMerger(aggregateMerger, ::getVersion)

    override suspend fun save(aggregate: T): T {
        return try {
            saveTransactional(aggregate)
        } catch (_: OptimisticLockingFailureException) {
            handleConflict(aggregate)
        }
    }

    override suspend fun applyOn(id: ID, version: Long, function: suspend T.() -> T): T? {
        val oldVersion = findVersion(id, version) ?: return null
        return save(oldVersion.function())
    }

    override suspend fun findById(id: ID): T? = aggregateRepository.findById(id.toString())

    override fun findAll(): Flow<T> = aggregateRepository.findAll()
    override fun findAllById(ids: Iterable<ID>) = aggregateRepository.findAllById(ids.map { it.toString() })

    /**
     * If there is no aggregate with given [id], return `null`.
     */
    override suspend fun findVersion(id: ID, version: Long): T? {
        val current = aggregateRepository.findById(id.toString()) ?: return null
        if (current.version == version) return current
        val history = historyRepository.findById(id) ?: return null
        return history.find(version)
    }

    override suspend fun deleteAll() = transactionalOperator.transactional {
        aggregateRepository.deleteAll()
        historyRepository.deleteAll()
    }

    private suspend fun handleConflict(aggregate: T): T {
        val merged = merger.merge(aggregateRepository.findById(aggregate.id!!.toString())!!, aggregate)
        return saveTransactional(merged)
    }

    private suspend fun saveTransactional(aggregate: T): T = transactionalOperator.transactional {
        if (!aggregate.isNew()) {
            pushCurrentToHistory(aggregate.id!!)
        }
        aggregateRepository.save(aggregate)
    }

    private suspend fun pushCurrentToHistory(id: ID): History<T, ID> {
        val current = aggregateRepository.findById(id.toString())
            ?: throw IllegalStateException("No aggregate found for id $id")
        val history: History<T, ID> = historyRepository.findById(id) ?: History(id)
        return historyRepository.save(history.add(current))
    }

    private suspend fun getVersion(id: ID, version: Long) =
        findVersion(id, version) ?: throw VersionNotFoundException(id.toString(), version)
}

class VersionNotFoundException(id: String, version: Long) : RuntimeException("Version $version in aggregate $id not found in history")
