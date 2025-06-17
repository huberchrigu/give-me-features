package ch.chrigu.gmf.givemefeatures.shared.markdown.converter

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.space.SFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

object MarkdownToHtmlConverter {
    private val flavor = SFMFlavourDescriptor()
    private val parser = MarkdownParser(flavor)

    fun convert(markdown: String): String {
        val root = parser.parse(IElementType("p"), markdown)
        return HtmlGenerator(markdown, root, flavor).generateHtml()
    }
}
