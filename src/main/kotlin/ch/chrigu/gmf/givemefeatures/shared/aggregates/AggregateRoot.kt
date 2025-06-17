package ch.chrigu.gmf.givemefeatures.shared.aggregates

interface AggregateRoot<ID> {
    val id: ID?
    val version: Long?

    fun isNew(): Boolean
}
