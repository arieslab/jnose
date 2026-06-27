package io.github.arieslab.pages.base

import org.apache.wicket.markup.html.WebPage

open class ImprimirPage : WebPage() {

    companion object {
        protected fun resolveRealPath(): String {
            return try {
                val app = org.apache.wicket.protocol.http.WebApplication.get()
                val realPath = app.servletContext.getRealPath("")
                if (realPath != null) realPath else System.getProperty("user.dir")
            } catch (_: Exception) {
                System.getProperty("user.dir")
            }
        }
    }
}
