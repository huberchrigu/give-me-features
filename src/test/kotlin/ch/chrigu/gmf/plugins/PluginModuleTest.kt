package ch.chrigu.gmf.plugins

import ch.chrigu.gmf.TestcontainersConfiguration
import ch.chrigu.gmf.shared.aggregates.AggregateRoot
import ch.chrigu.gmf.shared.security.runas.RunAs.runAs
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithMockUser

@ApplicationModuleTest
@Import(TestcontainersConfiguration::class, DummyPluginConfiguration::class)
class PluginModuleTest(
    private val pluginService: PluginService,
    @MockkBean private val featureDefinition: ParentDefinition<FeatureReference>,
    @MockkBean private val taskDefinition: ParentDefinition<TaskReference>
) {

    @Test
    @WithMockUser(roles = ["USER"])
    fun `should update data`() = runTest {
        val pluginStatusId = fetchId()
        withDefinition(featureDefinition, "/features", pluginStatusId) { featureItem }
        val feature = mockk<FeatureReference> {}
        val form = pluginService.update(feature, pluginStatusId, mapOf("activate" to true), featureDefinition)
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
        val task = mockk<TaskReference> {}
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

    private inline fun <reified T : AggregateRoot<*>> withDefinition(
        definition: ParentDefinition<T>,
        uriPrefix: String,
        pluginStatusId: PluginStatusId,
        crossinline getItem: PluginTouchpoints.() -> ItemDefinition<T, *>?
    ) {
        every { definition.getItemDefinition(any()) } answers {
            this.arg<Plugin>(0).touchpoints.getItem()
        }
        every { definition.uriFor(any(), any()) } returns "$uriPrefix/1/plugins/$pluginStatusId"
    }

    private suspend fun fetchId() = runAs("ADMIN") {
        pluginService.findAll().first().id
    }
}