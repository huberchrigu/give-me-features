package ch.chrigu.gmf.testing.plugin

import ch.chrigu.gmf.plugins.FeatureReference
import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.plugins.ItemDefinition
import ch.chrigu.gmf.plugins.ItemField
import ch.chrigu.gmf.plugins.ItemTriggers
import ch.chrigu.gmf.plugins.ItemType
import ch.chrigu.gmf.plugins.PluginRepository
import ch.chrigu.gmf.plugins.PluginTouchpoints
import ch.chrigu.gmf.plugins.TaskReference
import ch.chrigu.gmf.plugins.TaskReferenceId
import ch.chrigu.gmf.plugins.string
import ch.chrigu.gmf.testing.TestDefinition
import ch.chrigu.gmf.testing.TestReference
import ch.chrigu.gmf.testing.TestState
import ch.chrigu.gmf.testing.persistence.TestDefinitionRepository
import ch.chrigu.gmf.testing.persistence.toPluginRepository

class TestingTouchpointFactory(private val repository: TestDefinitionRepository) { // TODO: Should be much easier, enum should be supported as dropdown
    fun create() = PluginTouchpoints(
        featureItem = createFeatureItem(repository.toPluginRepository()),
        taskItem = createTaskItem(repository.toPluginRepository())
    )

    private fun createFeatureItem(pluginRepository: PluginRepository<TestDefinition, FeatureReferenceId>): ItemDefinition<FeatureReference, FeatureReferenceId, TestDefinition> =
        ItemDefinition(
            persistenceClass = TestDefinition::class.java,
            fields = listOf(
                ItemField("state", ItemType.TEXT, "State", { state.name })
            ),
            triggers = ItemTriggers { feature ->
                pluginRepository.findById(feature.id!!)?.let {
                    pluginRepository.save(it.copy(state = TestState.NOT_STARTED))
                }
            },
            repository = pluginRepository,
            fromMap = { feature: FeatureReference ->
                val state = parseState(string("state"))
                TestDefinition(
                    id = feature.id!!.toString(),
                    tests = listOf(TestReference(feature = feature.id, task = null)),
                    state = state
                )
            }
        )

    private fun createTaskItem(pluginRepository: PluginRepository<TestDefinition, TaskReferenceId>): ItemDefinition<TaskReference, TaskReferenceId, TestDefinition> =
        ItemDefinition(
            persistenceClass = TestDefinition::class.java,
            fields = listOf(
                ItemField("state", ItemType.TEXT, "State", { state.name })
            ),
            triggers = ItemTriggers { task ->
                pluginRepository.findById(task.id!!)?.let {
                    pluginRepository.save(it.copy(state = TestState.NOT_STARTED))
                }
            },
            repository = pluginRepository,
            fromMap = { task: TaskReference ->
                val state = parseState(string("state"))
                TestDefinition(
                    id = task.id!!.toString(),
                    tests = listOf(TestReference(feature = null, task = task.id)),
                    state = state
                )
            }
        )

    private fun parseState(value: String): TestState {
        return TestState.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: TestState.NOT_STARTED
    }
}