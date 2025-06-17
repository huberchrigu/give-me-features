package ch.chrigu.gmf.givemefeatures.shared.aggregates

import kotlinx.coroutines.flow.Flow

/**
 * Tracks changes on aggregates and allows listening to them. Only the latest change is emitted, intermediate values may be discarded.
 */
interface AggregateChanges<T, ID> {
    fun listen(id: ID): Flow<T>
}

interface AllAggregateChanges<T : AggregateRoot<ID>, ID> : AggregateChanges<T, ID> {
    fun listenToAll(): Flow<T>
}
