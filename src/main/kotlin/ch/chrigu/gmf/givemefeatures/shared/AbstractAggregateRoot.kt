package ch.chrigu.gmf.givemefeatures.shared

import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

abstract class AbstractAggregateRoot<ID>(
    @field:MongoId(targetType = FieldType.STRING) override val id: ID,
    @field:Version final override val version: Long?
) : AggregateRoot<ID> {

    final override fun isNew() = version == null
}