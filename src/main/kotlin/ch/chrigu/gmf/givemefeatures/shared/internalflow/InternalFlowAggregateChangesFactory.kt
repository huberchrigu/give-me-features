package ch.chrigu.gmf.givemefeatures.shared.internalflow

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot

//@Component
@Deprecated("Is not used anymore, as it does not support scaling")
class InternalFlowAggregateChangesFactory : AggregateChangesFactory {
    override fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>) = MultiAggregateChanges<T, ID>()
}
