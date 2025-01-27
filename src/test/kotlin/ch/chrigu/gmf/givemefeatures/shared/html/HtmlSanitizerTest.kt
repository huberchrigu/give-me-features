package ch.chrigu.gmf.givemefeatures.shared.html

import org.assertj.core.api.Assertions
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class HtmlSanitizerTest {
    @Test
    fun `should allow richtext`() {
        val richtext = """
            <p><img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAB8YAAAYCCAIAAADGaxQRAAAAAXNSR0IArs4c6QAAIABJREFUeAHs3f93VOW99//77zjrXuus9bnX/dMn675/iBxqre3poa1traW1taUcq+X05FRWTa32kFMXyNEqpSgqVqUoB0WqeFBQYcCoIAKBhAQC4UsCIQlJTCYh3zOZSWYyX/a+G3e48mbPl+wJs2fPNfNkddU9M3uu69qPvZPr2q9cc83/MPmHAAIIIIAAAggggAACCCCAAAIIIIAAAggggAACDgT+h4N92AUBBBBAAAEEEEAAAQQQQAABBBBAAAEEEEAAAQRMInUuAgQQQAABBBBAAAEEEEAAAQQQQAABBBBAAAEEHAkQqTtiYicEEEAAAQQQQAABBBBAAAEEEEAAAQQQQAABBIjUuQYQQAABBBBAAAEEEEAAAQQQQAABBBBAAAEEEHAkQKTuiImdEEAAAQQQQAABBBBAAAEEEEAAAQQQQAABBBAgUucaQAABBBBAAAEEEEAAAQQQQAABBBBAAAEEEEDAkQCRuiMmdkIAAQQQQAABBBBAAAEEEEAAAQQQQAABBBBAgEidawABBBBAAAEEEEAAAQQQQAABBBBAAAEEEEAAAUcCROqOmNgJAQQQQAABBBBAAAEEEEAAAQQQQAABBBBAAAEida4BBBBAAAEEEEAAAQQQQAABBBBAAAEEEEAAAQQcCRCpO2JiJwQQQAABBBBAAAEEEEAAAQQQQAABBBBAAAEEiNS5BhBAAAEEEEAAAQQQQAABBBBAA" width="406" height="314"></p>
            <p>a <strong>a <em>a&nbsp;</em></strong><em>a</em></p>
            <p style="text-align:right">asdf</p>
            <p style="text-align:center">asdf</p>
            <ol>
            <li style="text-align:center">asdf</li>
            <li style="text-align:center">asdf</li>
            </ol>
            <p style="text-align:center">&nbsp;</p>
            <ul>
            <li style="text-align:left">asdf</li>
            <li style="text-align:left">asdf</li>
            </ul>
            <p style="text-align:left"><span style="background-color:#f1c40f">asdf</span></p>
            <h1 style="text-align:left">asdf</h1>
            <pre style="text-align:left"><span style="background-color:#f1c40f">asdfasdf<br><br></span></pre>
            <table style="border-collapse:collapse;width:100%;background-color:#eccafa;border:1px dotted #fbeeb8" border="1"><colgroup><col style="width:25%"><col style="width:25%"><col style="width:25%"><col style="width:25%"></colgroup>
            <tbody>
            <tr>
            <td style="border-color:#fbeeb8">1</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            </tr>
            <tr>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">2</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            </tr>
            <tr>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            <td style="border-color:#fbeeb8">3</td>
            <td style="border-color:#fbeeb8">&nbsp;</td>
            </tr>
            </tbody>
            </table>
            <pre style="text-align:left">&nbsp;</pre>
        """.trimIndent()
        val result = HtmlSanitizer.sanitize(richtext)
        val expected = parse(richtext)
        Assertions.assertThat(parse(result)).isEqualTo(expected)
    }

    private fun parse(html: String) = Jsoup.parse(html).body().toString()
}
