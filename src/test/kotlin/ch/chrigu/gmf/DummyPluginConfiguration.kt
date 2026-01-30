package ch.chrigu.gmf

import ch.chrigu.gfm.plugin.*
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DummyPluginConfiguration {
    @Bean
    fun dummyPlugin() = Plugin(
        PluginId("dummy"), "Dummy", PluginTouchpoints(
            featureItem = DummyFeatureExtension.itemDefinition, // TODO: Test
            taskItem = DummyTaskExtension.itemDefinition // TODO: Test
        )
    )
}

data class DummyTaskExtension(val description: String) {
    companion object {
        val itemDefinition = ItemDefinition<TaskReference, DummyTaskExtension>(
            DummyTaskExtension::class.java,
            listOf(ItemField("description", ItemType.TEXT, "Dummy description", { description })),
            ItemTriggers() // TODO: Implement triggers
        ) { DummyTaskExtension(get("description") as String) }
    }
}

data class DummyFeatureExtension(val activate: Boolean) {
    companion object {
        val itemDefinition = ItemDefinition<FeatureReference, DummyFeatureExtension>(
            DummyFeatureExtension::class.java,
            listOf(ItemField("activate", ItemType.BOOLEAN, "Activate dummy feature", { activate })),
            ItemTriggers() // TODO: Implement triggers
        ) { DummyFeatureExtension(get("activate") as Boolean) }
    }
}