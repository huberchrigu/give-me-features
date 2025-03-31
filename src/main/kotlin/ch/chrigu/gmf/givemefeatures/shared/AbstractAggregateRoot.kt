package ch.chrigu.gmf.givemefeatures.shared

import org.springframework.data.annotation.Version

abstract class AbstractAggregateRoot<ID>(override val id: ID?, @field:Version final override val version: Long?) : AggregateRoot<ID> {
    init {
        if (isNew())
            require(version == null) { "Do not set a version manually. This is done by Spring Data when persisting the aggregate root." }
        else
            require(version != null) { "An already persisted aggregate root must always have a version." }
    }

    final override fun isNew() = id == null
}