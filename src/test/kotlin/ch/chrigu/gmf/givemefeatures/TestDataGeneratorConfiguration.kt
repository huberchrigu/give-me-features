package ch.chrigu.gmf.givemefeatures

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.Markdown
import ch.chrigu.gmf.givemefeatures.tasks.Task
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestDataGeneratorConfiguration {
    @Bean
    fun testDataGenerator(featureService: FeatureService) = ApplicationRunner {
        runBlocking {
            if (featureService.getFeatures().toList().isEmpty()) {
                featureService.newFeature(
                    Feature.describeNewFeature(
                        "Task status shall be changeable to blocked",
                        Markdown("A user can set a task in status **open** to status **blocked**")
                    )
                )
                    .let { featureService.addTask(it.id, it.version!!, Task.describeNewTask("Extend domain model"))!! }
                    .let { featureService.addTask(it.id, it.version!!, Task.describeNewTask("Extend api"))!! }
                    .let { featureService.addTask(it.id, it.version!!, Task.describeNewTask("Add controller method and ui extension")) }
            }
        }
    }
}