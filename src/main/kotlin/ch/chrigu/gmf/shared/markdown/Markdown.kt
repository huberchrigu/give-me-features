package ch.chrigu.gmf.shared.markdown

import ch.chrigu.gmf.shared.html.HtmlSanitizer
import ch.chrigu.gmf.shared.markdown.converter.MarkdownToHtmlConverter

data class Markdown(private val markdownContent: String) {
    fun toHtml() = MarkdownToHtmlConverter.convert(markdownContent)
        .let { HtmlSanitizer.sanitize(it) }

    operator fun plus(other: String) = Markdown(markdownContent + other)

    override fun toString(): String = markdownContent
}

fun String.toMarkdown() = Markdown(this)
