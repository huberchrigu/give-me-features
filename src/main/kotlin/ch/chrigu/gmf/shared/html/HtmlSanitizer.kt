package ch.chrigu.gmf.shared.html

import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.Sanitizers
import java.util.function.Predicate

object HtmlSanitizer {
    private val customImages = HtmlPolicyBuilder()
        .allowUrlProtocols("http", "https", "data").allowElements("img")
        .allowAttributes("alt", "src").onElements("img")
        .allowAttributes("border", "height", "width").matching(Predicate { it.toFloatOrNull() != null })
        .onElements("img")
        .toFactory()
    private val allowPre = HtmlPolicyBuilder().allowElements("pre").toFactory()
    private val customTables = HtmlPolicyBuilder()
        .allowStandardUrlProtocols()
        .allowElements(
            "table", "tr", "td", "th",
            "colgroup", "caption", "col",
            "thead", "tbody", "tfoot"
        )
        .allowAttributes("summary", "border").onElements("table")
        .allowAttributes("align", "valign")
        .onElements(
            "table", "tr", "td", "th",
            "colgroup", "col",
            "thead", "tbody", "tfoot"
        )
        .allowTextIn("table") // WIDGY
        .toFactory()
    private val sanitizer = Sanitizers.BLOCKS
        .and(customImages)
        .and(Sanitizers.STYLES)
        .and(customTables)
        .and(Sanitizers.LINKS)
        .and(Sanitizers.FORMATTING)
        .and(allowPre)

    fun sanitize(html: String): String = sanitizer.sanitize(html)
}
