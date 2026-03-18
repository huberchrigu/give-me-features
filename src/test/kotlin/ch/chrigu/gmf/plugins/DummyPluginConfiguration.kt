package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.plugins.mongo.toPluginRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@TestConfiguration
class DummyPluginConfiguration {
    @Bean
    fun dummyPlugin(
        dummyFeatureRepository: CoroutineCrudRepository<DummyFeatureExtension, String>,
        dummyTaskRepository: CoroutineCrudRepository<DummyTaskExtension, String>
    ) = Plugin(
        PluginId("dummy"), "Dummy", PluginTouchpoints(
            featureItem = DummyFeatureExtension.itemDefinition(dummyFeatureRepository.toPluginRepository()),
            taskItem = DummyTaskExtension.itemDefinition(dummyTaskRepository.toPluginRepository())
        )
    )
}

data class DummyTaskExtension(val id: TaskReferenceId, val description: String) {
    companion object {
        fun itemDefinition(dummyTaskRepository: PluginRepository<DummyTaskExtension, TaskReferenceId>) = ItemDefinition<TaskReference, TaskReferenceId, DummyTaskExtension>(
            DummyTaskExtension::class.java,
            listOf(ItemField("description", ItemType.TEXT, "Dummy description", { description })),
            ItemTriggers(), // TODO: Implement triggers
            dummyTaskRepository
        ) { DummyTaskExtension(get("id").toTaskId(), get("description") as String? ?: "") }

        private fun Any?.toTaskId() = object : TaskReferenceId {}
    }
}

data class DummyFeatureExtension(val id: FeatureReferenceId, val activate: Boolean) {
    companion object {
        fun itemDefinition(dummyFeatureRepository: PluginRepository<DummyFeatureExtension, FeatureReferenceId>) =
            ItemDefinition<FeatureReference, FeatureReferenceId, DummyFeatureExtension>(
                DummyFeatureExtension::class.java,
                listOf(ItemField("activate", ItemType.BOOLEAN, "Activate dummy feature", { activate })),
                ItemTriggers(), // TODO: Implement triggers
                dummyFeatureRepository
            ) { DummyFeatureExtension(get("id").toFeatureId(), get("activate") as Boolean? ?: false) }

        private fun Any?.toFeatureId() = object : FeatureReferenceId {}
    }
}

interface DummyTaskRepository : CoroutineCrudRepository<DummyTaskExtension, String>
interface DummyFeatureRepository : CoroutineCrudRepository<DummyFeatureExtension, String>