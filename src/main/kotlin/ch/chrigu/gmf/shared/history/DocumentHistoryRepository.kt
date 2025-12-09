package ch.chrigu.gmf.shared.history

import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query

class DocumentHistoryRepository<T : AggregateRoot<ID>, ID : Any>(private val mongoTemplate: ReactiveMongoTemplate, private val collection: String) {
    suspend fun save(history: History<T, ID>): History<T, ID> {
        return mongoTemplate.save(history, collection).awaitSingle()
    }

    suspend fun findById(id: ID): History<T, ID>? = mongoTemplate.findById(id, History::class.java, collection).awaitSingleOrNull() as History<T, ID>?

    suspend fun deleteAll() {
        mongoTemplate.remove(Query(), collection).awaitFirst()
    }
}
