package ch.chrigu.gmf.givemefeatures.shared

import kotlinx.coroutines.flow.Flow

/**
 * Tracks changes on aggregates and allows listening to them.
 */
interface AggregateChanges<T, ID> {
    fun listen(id: ID): Flow<T>
    suspend fun emitIfListened(id: ID, value: T)
}

interface AllAggregateChanges<T : AggregateRoot<ID>, ID> : AggregateChanges<T, ID> {
    fun listenToAll(): Flow<T>
}