package ch.chrigu.gmf.shared.aggregates

import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

abstract class AbstractAggregateRoot<ID>(
    @field:MongoId(targetType = FieldType.STRING) override val id: ID,
    @field:Version final override val version: Long?
) : AggregateRoot<ID> {

    final override fun isNew() = version == null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractAggregateRoot<*>) return false

        if (id != other.id) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (version?.hashCode() ?: 0)
        return result
    }
}
