package ch.chrigu.gmf.shared

import ch.chrigu.gmf.shared.markdown.Markdown
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class MarkdownTest {
    @Test
    fun `should not allow script tags`() {
        verifyConversion("<script>alert('You're hacked!')</script>", "\n")
    }

    @Test
    fun `should not allow javascript in markdown links`() {
        verifyConversion("[Click me](javascript:alert('You are hacked'))", "<p><a href=\"#\" rel=\"nofollow\">Click me</a></p>")
    }

    @Test
    fun `should embed in paragraph if no other block format is given`() {
        verifyConversion("Hello World!", "<p>Hello World!</p>")
    }

    @Test
    fun `should not embed in paragraph if block format is given`() {
        verifyConversion("<h1>Title</h1>", "<h1>Title</h1>\n")
    }

    @Test
    fun `should keep standard markdown formats`() {
        verifyConversion(
            """
            # Title
            
            This is **kotlin** `code`:
            
            ```kotlin
            val a = 1
            println "<script>alert(a)</script>"
            ```
            
            | Month    | Savings |
            | -------- | ------- |
            | January  | 250     |
            | February | 80      |
            | March    | 420     |
            
            * Option 1
            * Option 2
            * Option 3

        """.trimIndent(),
            """
                <h1>Title</h1>
                <p>This is <strong>kotlin</strong> <code>code</code>:</p>
                
                <pre><code>val a = 1
                println "&lt;script&gt;alert(a)&lt;/script&gt;"
                </code></pre>
                
                <table>
                <thead><tr><th>Month</th><th>Savings</th></tr></thead>
                <tbody>
                <tr><td>January</td><td>250</td></tr>
                <tr><td>February</td><td>80</td></tr>
                <tr><td>March</td><td>420</td></tr>
                </tbody>
                </table>
                
                <ul>
                <li>Option 1</li>
                <li>Option 2</li>
                <li>Option 3</li>
                </ul>
            """.trimIndent()
        )
    }

    private fun verifyConversion(markdown: String, expectedHtml: String) {
        val testee = Markdown(markdown)
        val actual = Jsoup.parse(testee.toHtml()).body().toString()
        assertThat(actual).isEqualTo(Jsoup.parse(expectedHtml).body().toString())
    }
}
