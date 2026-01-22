package ch.chrigu.gmf.features.shared.test

import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Page

fun Page.has(cssSelector: String) = ElementsDsl(querySelectorAll(cssSelector))
class ElementsDsl(val elements: List<ElementHandle>) {
    infix fun withText(text: String) = elements.any { it.textContent() == text }
}