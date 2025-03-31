package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.history.merge.HistoryMerger
import ch.chrigu.gmf.givemefeatures.shared.mongo.transactional
import kotlinx.coroutines.flow.Flow
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.transaction.ReactiveTransactionManager

abstract class AbstractHistoryRepository<T : AggregateRoot<ID>, ID : Any>(
    private val aggregateRepository: CoroutineCrudRepository<T, String>,
    private val historyRepository: CoroutineCrudRepository<History<T, ID>, String>,
    aggregateMerger: AggregateMerger<T, ID>,
    private val transactionManager: ReactiveTransactionManager
) : HistoryRepository<T, ID> {
    private val merger = HistoryMerger(aggregateMerger, ::getVersion)

    override suspend fun save(aggregate: T): T {
        try {
            return saveTransactional(aggregate)
        } catch (e: OptimisticLockingFailureException) {
            val merged = merger.merge(aggregateRepository.findById(aggregate.id!!.toString())!!, aggregate)
            return saveTransactional(merged)
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
        val history = historyRepository.findById(id.toString()) ?: return null
        return history.find(version)
    }

    override suspend fun deleteAll() = transactionManager.transactional {
        aggregateRepository.deleteAll()
        historyRepository.deleteAll()
    }

    private suspend fun saveTransactional(aggregate: T): T = transactionManager.transactional {
        if (!aggregate.isNew()) {
            pushCurrentToHistory(aggregate.id!!)
        }
        aggregateRepository.save(aggregate)
    }

    private suspend fun pushCurrentToHistory(id: ID): History<T, ID> {
        val current = aggregateRepository.findById(id.toString()) ?: throw IllegalStateException("No aggregate found for id $id")
        val history = historyRepository.findById(id.toString()) ?: History()
        return historyRepository.save(history.add(current))
    }

    private suspend fun getVersion(id: ID, version: Long) = findVersion(id, version) ?: throw VersionNotFoundException(id.toString(), version)
}

class VersionNotFoundException(id: String, version: Long) : RuntimeException("Version $version in aggregate $id not found in history")
