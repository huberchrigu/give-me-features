package ch.chrigu.gmf.givemefeatures.shared

interface AggregateRoot<ID> {
    val id: ID?
    val version: Long?

    fun isNew(): Boolean
}