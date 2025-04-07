package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import kotlinx.coroutines.flow.Flow

interface HistoryRepository<T : AggregateRoot<ID>, ID> {
    suspend fun save(aggregate: T): T
    suspend fun applyOn(id: ID, version: Long, function: suspend T.() -> T): T?
    suspend fun findById(id: ID): T?
    fun findAll(): Flow<T>
    fun findAllById(ids: Iterable<ID>): Flow<T>
    suspend fun findVersion(id: ID, version: Long): T?
    suspend fun deleteAll()
}
