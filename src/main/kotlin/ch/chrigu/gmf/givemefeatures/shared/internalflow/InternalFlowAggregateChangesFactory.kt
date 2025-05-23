package ch.chrigu.gmf.givemefeatures.shared.internalflow

import ch.chrigu.gmf.givemefeatures.shared.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot

class InternalFlowAggregateChangesFactory : AggregateChangesFactory {
    override fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>) = MultiAggregateChanges<T, ID>()
}