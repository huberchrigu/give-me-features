package ch.chrigu.gmf.givemefeatures.shared.web

import com.microsoft.playwright.*
import org.jetbrains.kotlin.incremental.createDirectory
import org.junit.jupiter.api.extension.*
import org.springframework.test.util.ReflectionTestUtils
import java.io.File
import java.nio.file.Paths

class SaveArtifactsOnFailure : TestWatcher, BeforeEachCallback, BeforeAllCallback, AfterAllCallback {
    private lateinit var playwright: Playwright
    private lateinit var browser: Browser
    private lateinit var context: BrowserContext
    private lateinit var page: Page

    override fun beforeAll(ctx: ExtensionContext) {
        setupAll()
    }

    override fun afterAll(context: ExtensionContext) {
        tearDownAll()
    }

    override fun beforeEach(ctx: ExtensionContext) {
        context = browser.newContext()
        context.tracing().start(Tracing.StartOptions().setScreenshots(true).setSnapshots(true))
        page = context.newPage()
        injectPage(ctx.requiredTestInstance)
    }

    override fun testFailed(ctx: ExtensionContext, cause: Throwable?) {
        val name = ctx.requiredTestMethod.name.replace(' ', '_')
        page.screenshot(Page.ScreenshotOptions().setPath(Paths.get("build/screenshots/$name.png")))
        context.tracing().stop(Tracing.StopOptions().setPath(Paths.get("build/traces/$name.zip")))
        File(File("build/html").also { it.createDirectory() }, "$name.html")
            .writeText(page.content())
        context.close()
    }

    override fun testSuccessful(ctx: ExtensionContext) {
        context.close()
    }

    private fun setupAll() {
        playwright = Playwright.create()
        browser = playwright.chromium().launch()
    }

    private fun tearDownAll() {
        browser.close()
        playwright.close()
    }

    private fun injectPage(testObject: Any) {
        ReflectionTestUtils.setField(testObject, null, page, Page::class.java)
    }
}
