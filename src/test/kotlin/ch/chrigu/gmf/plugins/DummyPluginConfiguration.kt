package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.plugins.mongo.toPluginRepository
import ch.chrigu.gmf.tasks.TaskId
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
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

data class DummyTaskExtension(@MongoId(targetType = FieldType.STRING) val id: TaskId, val description: String) {
    companion object {
        fun itemDefinition(dummyTaskRepository: PluginRepository<DummyTaskExtension, TaskReferenceId>) = ItemDefinition(
            DummyTaskExtension::class.java,
            listOf(ItemField("description", ItemType.TEXT, "Dummy description", { description })),
            ItemTriggers(DummyTaskTrigger(dummyTaskRepository)),
            dummyTaskRepository
        ) { DummyTaskExtension(TaskId(it.id!!.toString()), string("description")) }
    }
}

data class DummyFeatureExtension(@MongoId(targetType = FieldType.STRING) val id: FeatureId, val activate: Boolean) {
    companion object {
        fun itemDefinition(dummyFeatureRepository: PluginRepository<DummyFeatureExtension, FeatureReferenceId>) = ItemDefinition(
            DummyFeatureExtension::class.java,
            listOf(ItemField("activate", ItemType.BOOLEAN, "Activate dummy feature", { activate })),
            ItemTriggers(DummyFeatureTrigger(dummyFeatureRepository)),
            dummyFeatureRepository
        ) { DummyFeatureExtension(FeatureId(it.id!!.toString()), boolean("activate")) }
    }
}

interface DummyTaskRepository : CoroutineCrudRepository<DummyTaskExtension, String>
interface DummyFeatureRepository : CoroutineCrudRepository<DummyFeatureExtension, String>
class DummyFeatureTrigger(private val dummyFeatureRepository: PluginRepository<DummyFeatureExtension, FeatureReferenceId>) : OnChangeTrigger<FeatureReference> {
    override suspend fun invoke(feature: FeatureReference) {
        dummyFeatureRepository.findById(feature.id!!)?.let { dummyFeatureRepository.save(it.copy(activate = false)) }
    }
}

class DummyTaskTrigger(private val dummyTaskRepository: PluginRepository<DummyTaskExtension, TaskReferenceId>) : OnChangeTrigger<TaskReference> {
    override suspend fun invoke(task: TaskReference) {
        dummyTaskRepository.findById(task.id!!)?.let { dummyTaskRepository.save(it.copy(description = it.description + " changed")) }
    }
}