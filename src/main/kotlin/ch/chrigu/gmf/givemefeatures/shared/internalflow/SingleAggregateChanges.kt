package ch.chrigu.gmf.givemefeatures.shared.internalflow

import ch.chrigu.gmf.givemefeatures.shared.AggregateChanges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.ConcurrentHashMap

@Deprecated("Use mongo implementation")
class SingleAggregateChanges<T, ID> : AggregateChanges<T, ID> {
    private val emittedPerId = ConcurrentHashMap<ID, MutableSharedFlow<T>>()

    override fun listen(id: ID): Flow<T> = getOrCreate(id).asSharedFlow()

    override suspend fun emitIfListened(id: ID, value: T) {
        emittedPerId[id]?.emit(value)
    }

    private fun getOrCreate(id: ID) = emittedPerId.getOrPut(id) {
        MutableSharedFlow()
    }
}