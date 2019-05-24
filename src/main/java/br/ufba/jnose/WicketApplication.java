package br.ufba.jnose;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import br.ufba.jnose.pages.HomePage;

public class WicketApplication extends WebApplication {
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public void init() {
        super.init();
        this.getMarkupSettings().setStripWicketTags(true);
    }
}
