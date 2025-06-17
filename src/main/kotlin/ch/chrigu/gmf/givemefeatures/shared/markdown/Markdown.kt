package ch.chrigu.gmf.givemefeatures.shared.markdown

import ch.chrigu.gmf.givemefeatures.shared.html.HtmlSanitizer
import ch.chrigu.gmf.givemefeatures.shared.markdown.converter.MarkdownToHtmlConverter

data class Markdown(private val markdownContent: String) {
    fun toHtml() = MarkdownToHtmlConverter.convert(markdownContent)
        .let { HtmlSanitizer.sanitize(it) }

    operator fun plus(other: String) = Markdown(markdownContent + other)

    override fun toString(): String = markdownContent
}
