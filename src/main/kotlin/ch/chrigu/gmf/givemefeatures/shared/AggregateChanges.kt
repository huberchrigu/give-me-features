package ch.chrigu.gmf.givemefeatures.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import java.util.concurrent.ConcurrentHashMap

class SingleAggregateChanges<T, ID> : AggregateChanges<T, ID> {
    private val emittedPerId = ConcurrentHashMap<ID, MutableSharedFlow<T>>()// TODO: Make stateless & clean up flows

    override fun listen(id: ID): Flow<T> = getOrCreate(id).asSharedFlow()

    override suspend fun emitIfListened(id: ID, value: T) {
        emittedPerId[id]?.emit(value)
    }

    private fun getOrCreate(id: ID) = emittedPerId.getOrPut(id) {
        MutableSharedFlow()
    }
}

class MultiAggregateChanges<T : AggregateRoot<ID>, ID> : AggregateChanges<T, ID> {
    private val emittedAll = MutableSharedFlow<T>()

    fun listenToAll(): Flow<T> {
        return emittedAll.asSharedFlow()
    }

    override suspend fun emitIfListened(id: ID, value: T) {
        emittedAll.emit(value)
    }

    override fun listen(id: ID): Flow<T> {
        return emittedAll.filter { it.id == id }
    }
}

/**
 * Tracks changes on aggregates and allows listening to them.
 */
sealed interface AggregateChanges<T, ID> {
    fun listen(id: ID): Flow<T>
    suspend fun emitIfListened(id: ID, value: T)
}
