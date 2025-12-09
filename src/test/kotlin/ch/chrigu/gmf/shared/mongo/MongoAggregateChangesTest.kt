package ch.chrigu.gmf.shared.mongo

import ch.chrigu.gmf.TestcontainersConfiguration
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.TestConstructor
import kotlin.time.Duration.Companion.seconds

@DataMongoTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(TestcontainersConfiguration::class)
class MongoAggregateChangesTest(private val mongoTemplate: ReactiveMongoTemplate) {

    @Test
    fun testListenToAll() = runTest {
        val check = mutableSetOf<Int>()
        val job = launch(Dispatchers.IO) {
            MongoAggregateChanges(mongoTemplate, TestDocument::class.java).listenToAll()
                .collect { check.add(it.id) }
        }
        launch(Dispatchers.IO) {
            (0 until 100).onEach { mongoTemplate.save(TestDocument(it)).awaitSingle() }
        }

        await atMost 3.seconds until { check.contains(99) }
        job.cancel()
    }

    class TestDocument(override val id: Int) : AggregateRoot<Int> {
        override val version: Long
            get() = throw UnsupportedOperationException()

        override fun isNew(): Boolean {
            throw UnsupportedOperationException()
        }
    }
}
