package ch.chrigu.gmf.givemefeatures.shared

import ch.chrigu.gmf.givemefeatures.shared.html.HtmlSanitizer
import ch.chrigu.gmf.givemefeatures.shared.markdown.MarkdownToHtmlConverter

data class Markdown(private val markdownContent: String) {
    fun toHtml() = MarkdownToHtmlConverter.convert(markdownContent)
        .let { HtmlSanitizer.sanitize(it) }

    override fun toString(): String = markdownContent
}
