package ch.chrigu.gmf.givemefeatures.shared

interface AggregateChangesFactory {
    fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>): AllAggregateChanges<T, ID>
}
