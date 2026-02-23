package ch.chrigu.gmf.plugins.web

import ch.chrigu.gmf.plugins.PluginId
import ch.chrigu.gmf.plugins.PluginMetadata
import ch.chrigu.gmf.plugins.PluginService
import ch.chrigu.gmf.plugins.PluginStatus
import ch.chrigu.gmf.plugins.PluginStatusId
import ch.chrigu.gmf.shared.web.SharedUiActions.login
import ch.chrigu.gmf.shared.web.UiTest
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.LoadState
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@UiTest(PluginStatusController::class)
class PluginStatusControllerUiTest(
    @MockkBean private val pluginService: PluginService
) {
    private lateinit var page: Page

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun withPlugin() {
        val statusId = PluginStatusId("1")
        val plugin = PluginStatus(true, PluginMetadata(PluginId("123"), "Plugin"), statusId, 0L)
        every { pluginService.findAll() } returns flowOf(plugin)
        coEvery { pluginService.deactivatePlugin(statusId, 0L) } returns plugin.deactivate(0L)
    }

    @Test
    fun `should show and deactivate plugin status`() {
        openPluginPage {
            assertPlugin()
            deactivatePlugin()
            assertPlugin(false)
        }
    }

    private fun Page.assertPlugin(activated: Boolean = true) {
        val plugin = querySelector(".list-group-item")
        val checkbox = plugin.querySelector(".form-check-input")
        assertThat(plugin.textContent().trim()).isEqualTo("Plugin")
        assertThat(checkbox.isChecked).isEqualTo(activated)
    }

    private fun Page.deactivatePlugin() {
        val checkbox = querySelector(".form-check-input")
        checkbox.click()
        waitForLoadState(LoadState.NETWORKIDLE)
    }

    private fun openPluginPage(test: Page.() -> Unit) {
        page.navigate("http://localhost:$port/plugins")
        page.login("admin")
        test(page)
    }
}