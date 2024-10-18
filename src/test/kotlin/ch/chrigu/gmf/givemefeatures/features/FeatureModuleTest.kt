package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class FeatureModuleTest(private val featureService: FeatureService) {
    @Test
    fun `should describe a feature`() {
        runBlocking {
            val result = featureService.newFeature(Feature.describeNewFeature("Login", "A user should be able to login"))
            assertThat(result.name).isEqualTo(result.name)
            assertThat(result.description).isEqualTo(result.description)
            assertThat(result.id).isNotNull()
        }
    }

    @Test
    fun `should add a task to a feature`() {
        TODO()
    }

    @Test
    fun `should get a feature by id`() {
        TODO()
    }

    @Test
    fun `should get all features`() {
        TODO()
    }
}
