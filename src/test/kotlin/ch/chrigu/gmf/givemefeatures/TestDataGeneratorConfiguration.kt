package ch.chrigu.gmf.givemefeatures

import ch.chrigu.gmf.givemefeatures.features.Feature
import ch.chrigu.gmf.givemefeatures.features.FeatureService
import ch.chrigu.gmf.givemefeatures.shared.markdown.Markdown
import ch.chrigu.gmf.givemefeatures.tasks.Task
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestDataGeneratorConfiguration {
    @Bean
    fun testDataGenerator(featureService: FeatureService) = ApplicationRunner {
        val markdownContent =
            "# A header\n## Another header\n### Yet another header\nA series of rich text content:\n- an itemized list\n- with several elements\n\n1. a numbered list\n1. with several numbers\n\n> A quote\n> > A quote within a quote\n\n A **bold text**, an _italic text_, a ~~strikethrough~~, [a link](https://www.google.ch), a simple `inline code` and an image: ![an image](https://www.google.ch/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png)\n\n```\nvar s = \"Hi There!\";\nalert(s)\n```\n\nA table:\n\n|First Column|Second Column|\n|-|-|\n|A1|A2|\n|B1|B2|"
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
                // Using https://www.jetbrains.com/help/space/markdown-syntax.html as a reference
                featureService.newFeature(
                    Feature.describeNewFeature(
                        "Rich Text Feature",
                        Markdown(markdownContent)
                    )
                )
                    .let { featureService.addTask(it.id, it.version!!, Task.describeNewTask("Rich Text Task",markdownContent))!! }
            }
        }
    }
}
