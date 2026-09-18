package ch.chrigu.gmf.shared.mongo

import ch.chrigu.gmf.shared.aggregates.AggregateChangesFactory
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.aggregates.AllAggregateChanges
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component
import reactor.core.Disposable
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * delete operations are ignored because they have no body.
 */
class MongoAggregateChanges<T : AggregateRoot<ID>, ID>(mongoTemplate: ReactiveMongoTemplate, clazz: Class<T>) : AllAggregateChanges<T, ID> {
    private val flow = MutableSharedFlow<T>()
    private val subscription: Disposable = mongoTemplate.changeStream(clazz)
        .watchCollection(mongoTemplate.getCollectionName(clazz))
        .listen()
        .filter { it.body != null }
        .subscribe(
            { runBlocking { flow.emit(it.body!!) } },
            { error -> logger.warn("Mongo change stream stopped", error) }
        )

    fun stop() = subscription.dispose()

    override fun listen(id: ID): Flow<T> {
        return flow.filter { it.id == id }.conflate()
    }

    override fun listenToAll(conflated: Boolean): Flow<T> {
        return if (conflated) flow.conflate() else flow
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(MongoAggregateChanges::class.java)
    }
}

@Component
class MongoAggregateChangesFactory(private val mongoTemplate: ReactiveMongoTemplate) : AggregateChangesFactory {
    private val aggregateChanges = ConcurrentLinkedQueue<MongoAggregateChanges<*, *>>()

    override fun <T : AggregateRoot<ID>, ID> create(clazz: Class<T>): AllAggregateChanges<T, ID> = MongoAggregateChanges(mongoTemplate, clazz).also { aggregateChanges.add(it) }

    @PreDestroy
    fun stop() = aggregateChanges.forEach { it.stop() }
}
