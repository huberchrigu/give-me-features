package ch.chrigu.gmf.features.repository.mongo

import ch.chrigu.gmf.TestcontainersConfiguration
import ch.chrigu.gmf.features.Feature
import ch.chrigu.gmf.shared.markdown.Markdown
import ch.chrigu.gmf.shared.mongo.MongoCustomConfiguration
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

@DataMongoTest
@Import(TestcontainersConfiguration::class, MongoCustomConfiguration::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CoroutineFeatureRepositoryTest(private val testee: CoroutineFeatureRepository) {
    @Test
    fun `should persist feature`() = runTest {
        val saved =
            testee.save(
                Feature.describeNewFeature(
                    "Hello world",
                    Markdown("Each programmer needs to start with this feature")
                )
            )
        assertThat(saved.id).isNotNull
        assertThat(saved.version).isNotNull
        assertThat(testee.findById(saved.id.toString())).isNotNull
    }
}
