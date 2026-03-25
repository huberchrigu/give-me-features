package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.TestcontainersConfiguration
import ch.chrigu.gmf.features.FeatureId
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.security.runas.RunAs.runAs
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithMockUser

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class, DummyPluginConfiguration::class)
class PluginModuleTest(
    private val pluginService: PluginService,
    private val dummyFeatureRepository: DummyFeatureRepository,
    private val featureDefinition: ParentDefinition<FeatureReference, FeatureReferenceId>,
    private val taskDefinition: ParentDefinition<TaskReference, TaskReferenceId>,
    private val featureFlow: MutableSharedFlow<FeatureReference>
) {
    @Test
    @WithMockUser(roles = ["USER"])
    fun `should update data`() = runTest {
        val pluginStatusId = fetchId()
        withDefinition(featureDefinition, "/features", pluginStatusId) { featureItem }
        val feature = mockk<FeatureReference> {
            every { id } returns mockk()
        }
        val form = pluginService.update(feature, pluginStatusId, mapOf("activate" to listOf("on")), featureDefinition)
        assertThat(form).isEqualTo(
            PluginForm<FeatureReference>(
                "Dummy", listOf(
                    PluginFormField("activate", "Activate dummy feature", "checkbox", required = true, readOnly = false, true)
                ), "/features/1/plugins/$pluginStatusId"
            )
        )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `should get forms`() = runTest {
        val pluginStatusId = fetchId()
        withDefinition(taskDefinition, "/tasks", pluginStatusId) { taskItem }
        val taskId = mockk<TaskReferenceId>()
        val task = mockk<TaskReference> {
            every { id } returns taskId
        }
        val forms = pluginService.getForms(task, taskDefinition)
        assertThat(forms).containsExactly(
            PluginForm<TaskReference>(
                "Dummy", listOf(
                    PluginFormField("description", "Dummy description", "text", required = true, readOnly = false, "")
                ), "/tasks/1/plugins/$pluginStatusId"
            )
        )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `should not allow access too plugin status`() = runTest {
        assertThrows<AccessDeniedException> { pluginService.findAll().toList() }
    }

    @Test
    fun `should modify feature on update`() = runTest {
        val featureId = FeatureId()
        val feature = mockk<FeatureReference> {
            every { id } returns featureId
        }
        dummyFeatureRepository.save(DummyFeatureExtension(featureId, true))
        featureFlow.emit(feature)
        await.until {
            runBlocking {
                dummyFeatureRepository.findById(featureId.toString())?.activate == false
            }
        }
    }

    private inline fun <reified T : AggregateRoot<ID>, ID> withDefinition(
        definition: ParentDefinition<T, ID>,
        uriPrefix: String,
        pluginStatusId: PluginStatusId,
        crossinline getItem: PluginTouchpoints.() -> ItemDefinition<T, ID, *>?
    ) {
        every<ItemDefinition<T, ID, *>?> { definition.getItemDefinition(any()) } answers {
            this.arg<Plugin>(0).touchpoints.getItem()
        }
        every { definition.uriFor(any(), any()) } returns "$uriPrefix/1/plugins/$pluginStatusId"
    }

    private suspend fun fetchId() = runAs("ADMIN") {
        pluginService.findAll().first().id
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun featureFlow() = MutableSharedFlow<FeatureReference>()

        @Bean
        fun featureDefinition(featureFlow: MutableSharedFlow<FeatureReference>): ParentDefinition<FeatureReference, FeatureReferenceId> = mockk {
            every { changes } returns featureFlow
            every { getItemDefinition(any()) } answers { arg<Plugin>(0).touchpoints.featureItem }
        }

        @Bean
        fun taskDefinition(): ParentDefinition<TaskReference, TaskReferenceId> = mockk {
            every { changes } returns emptyFlow()
            every { getItemDefinition(any()) } answers { arg<Plugin>(0).touchpoints.taskItem }
        }
    }
}