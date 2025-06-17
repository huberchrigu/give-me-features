package ch.chrigu.gmf.givemefeatures.shared.internalflow

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AllAggregateChanges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter

@Deprecated("Use mongo implementation")
class MultiAggregateChanges<T : AggregateRoot<ID>, ID> : AllAggregateChanges<T, ID> {
    private val emittedAll = MutableSharedFlow<T>()

    override fun listenToAll(): Flow<T> {
        return emittedAll.asSharedFlow()
    }

    override fun listen(id: ID): Flow<T> {
        return emittedAll.filter { it.id == id }
    }
}
