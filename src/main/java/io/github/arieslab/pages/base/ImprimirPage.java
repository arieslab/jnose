package io.github.arieslab.pages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import java.io.File;

public class ImprimirPage extends WebPage {
    private static final long serialVersionUID = 1L;
    public ImprimirPage() {}

    protected static String resolveRealPath() {
        try {
            var app = (WebApplication) org.apache.wicket.Application.get();
            var realPath = app.getServletContext().getRealPath("");
            if (realPath != null) return realPath;
        } catch (Exception ignored) {}
        return System.getProperty("user.dir");
    }
}
