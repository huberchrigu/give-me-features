package ch.chrigu.gmf.shared.web

import com.microsoft.playwright.Page

object SharedUiActions {
    fun Page.login(user: String = "user") {
        querySelector("input[name='username']").fill(user)
        querySelector("input[name='password']").fill(user)
        querySelector("button[type='submit']").click()
        waitForCondition { querySelector("input[name='username']") == null }
    }
}