package ch.chrigu.gmf.givemefeatures.features

import ch.chrigu.gmf.givemefeatures.TestcontainersConfiguration
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class)
class FeatureModuleTest(private val featureRepository: FeatureRepository) {
    @Test
    fun `should describe a feature`() {
        runBlocking {
            val feature = Feature.describeNewFeature("Login", "A user should be able to login")
            val result = featureRepository.save(feature)
            assertThat(result.name).isEqualTo(feature.name)
            assertThat(result.description).isEqualTo(feature.description)
            assertThat(result.id).isNotNull()
        }
    }
}
