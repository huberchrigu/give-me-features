package ch.chrigu.gmf.givemefeatures.shared.mongo

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.aggregates.AllAggregateChanges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component

/**
 * delete operations are ignored, because they have no body.
 */
class MongoAggregateChanges<T : AggregateRoot<ID>, ID>(mongoTemplate: ReactiveMongoTemplate, clazz: Class<T>) : AllAggregateChanges<T, ID> {
    private val flow = MutableSharedFlow<T>()

    init {
        mongoTemplate.changeStream<T>(clazz)
            .watchCollection(mongoTemplate.getCollectionName(clazz))
            .listen()
            .filter { it.body != null }
            .subscribe { runBlocking { flow.emit(it.body!!) } }
    }

    override fun listen(id: ID): Flow<T> {
        return flow.filter { it.id == id }.conflate()
    }

    override fun listenToAll(): Flow<T> {
        return flow.conflate()
    }
}

@Component
class MongoAggregateChangesFactory(private val mongoTemplate: ReactiveMongoTemplate) : AggregateChangesFactory {
    override fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>) = MongoAggregateChanges(mongoTemplate, clazz)
}
