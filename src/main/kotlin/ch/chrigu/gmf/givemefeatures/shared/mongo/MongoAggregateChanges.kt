package ch.chrigu.gmf.givemefeatures.shared.mongo

import ch.chrigu.gmf.givemefeatures.shared.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.AllAggregateChanges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

// TODO: Test how many open connections
class MongoAggregateChanges<T : AggregateRoot<ID>, ID>(private val mongoTemplate: ReactiveMongoTemplate, private val clazz: Class<T>) : AllAggregateChanges<T, ID> {
    override fun listen(id: ID): Flow<T> {
        return mongoTemplate.changeStream<T>(clazz)
            .watchCollection(mongoTemplate.getCollectionName(clazz))
            .filter(Criteria.where("_id").isEqualTo(id))
            .listen().asFlow()
            .mapNotNull { it.body }
    }

    override suspend fun emitIfListened(id: ID, value: T) {
        // TODO: noting to do
    }

    override fun listenToAll(): Flow<T> {
        return mongoTemplate.changeStream<T>(clazz)
            .watchCollection(mongoTemplate.getCollectionName(clazz))
            .listen().asFlow()
            .mapNotNull { it.body }
    }
}

@Component
class MongoAggregateChangesFactory(private val mongoTemplate: ReactiveMongoTemplate) : AggregateChangesFactory {
    override fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>) = MongoAggregateChanges(mongoTemplate, clazz)
}
