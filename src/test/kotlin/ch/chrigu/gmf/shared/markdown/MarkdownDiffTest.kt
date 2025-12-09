package ch.chrigu.gmf.shared.markdown

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarkdownDiffTest {
    @Test
    fun `should compute diff`() {
        val result = MarkdownDiff.diff(
            Markdown("**bold**\nsecond line\nthird line"),
            Markdown("bold\nsecond line\n3rd line\n4th line")
        )
        assertThat(result.toString()).isEqualTo(
            """
            ~**~bold~**~
            second line
            ~third~**3rd** line
            **4th line**
        """.trimIndent()
        )
    }

    @Test
    fun `should compute merge3`() {
        val result = MarkdownDiff.merge3(
            Markdown("**bold**\nsecond line\nthird line"),
            Markdown("bold\nsecond line\n3rd line\n4th line"),
            Markdown("second line\n**third line**")
        ).toString()
        assertThat(result).isEqualTo(
            """
                <<<<<<< OURS
                bold
                =======
                >>>>>>> THEIRS
                second line
                <<<<<<< OURS
                3rd line
                4th line
                =======
                **third line**
                >>>>>>> THEIRS
        """.trimIndent()
        )
    }
}
