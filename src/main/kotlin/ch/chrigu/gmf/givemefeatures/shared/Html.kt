package ch.chrigu.gmf.givemefeatures.shared

import ch.chrigu.gmf.givemefeatures.shared.html.HtmlSanitizer

class Html(value: String) {
    private val sanitized = HtmlSanitizer.sanitize(value)

    override fun toString(): String = sanitized

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Html

        return sanitized == other.sanitized
    }

    override fun hashCode(): Int {
        return sanitized.hashCode()
    }
}
