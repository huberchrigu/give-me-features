package ch.chrigu.gmf.givemefeatures

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.Html
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
                val feature = featureService.newFeature(
                    Feature.describeNewFeature(
                        "Task status shall be changeable to blocked",
                        Html("A user can set a task in status <b>open</b> to status <b>blocked</b>")
                    )
                )
                featureService.addTask(feature.id!!, Task.describeNewTask("Extend domain model"))
                featureService.addTask(feature.id!!, Task.describeNewTask("Extend api"))
                featureService.addTask(feature.id!!, Task.describeNewTask("Add controller method and ui extension"))
            }
        }
    }
}