package ch.chrigu.gmf.shared.aggregates

interface AggregateChangesFactory {
    fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>): AllAggregateChanges<T, ID>
}
