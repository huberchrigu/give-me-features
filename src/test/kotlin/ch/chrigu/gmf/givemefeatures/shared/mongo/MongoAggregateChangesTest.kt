package ch.chrigu.gmf.givemefeatures.shared.mongo

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.TestConstructor
import java.time.Duration

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

        await().atMost(Duration.ofSeconds(3)).until { check.contains(99) }
        job.cancel()
    }

    class TestDocument(override val id: Int) : AggregateRoot<Int> {
        override val version: Long?
            get() = TODO("Not yet implemented")

        override fun isNew(): Boolean {
            TODO("Not yet implemented")
        }
    }
}
