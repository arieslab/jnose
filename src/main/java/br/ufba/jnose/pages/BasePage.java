package br.ufba.jnose.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

public class BasePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public BasePage() {

        Link linkHome = new Link<String>("linkHome") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };

        Link linkEvolution = new Link<String>("linkEvolution") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };

        Link linkResults = new Link<String>("linkResults") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };

        add(linkHome);
        add(linkEvolution);
        add(linkResults);

    }

}
