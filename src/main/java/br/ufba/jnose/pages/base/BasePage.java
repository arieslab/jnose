package br.ufba.jnose.pages.base;

import br.ufba.jnose.pages.ByTestSmellsPage;
import br.ufba.jnose.pages.ConfigPage;
import br.ufba.jnose.pages.EvolutionPage;
import br.ufba.jnose.pages.HomePage;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

public class BasePage extends WebPage {
    private static final long serialVersionUID = 1L;

    private Label footTime;

    public BasePage() {

        footTime = new Label("footTimeHome");
        footTime.setOutputMarkupId(true);
        footTime.setOutputMarkupPlaceholderTag(true);
        add(footTime);

        Link linkHome = new Link<String>("linkHome") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        };

        Link linkByTestSmells = new Link<String>("linkByTestSmells") {
            @Override
            public void onClick() {
                setResponsePage(ByTestSmellsPage.class);
            }
        };

        Link linkEvolution = new Link<String>("linkEvolution") {
            @Override
            public void onClick() {
                setResponsePage(EvolutionPage.class);
            }
        };

        Link linkConfig = new Link<String>("linkConfig") {
            @Override
            public void onClick() {
                setResponsePage(ConfigPage.class);
            }
        };

        add(linkHome);
        add(linkEvolution);
        add(linkConfig);
        add(linkByTestSmells);

        AbstractAjaxTimerBehavior timerHome = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                footTime.setDefaultModel(Model.of(cont + ""));
                cont++;
                target.add(footTime);
            }
        };
        add(timerHome);

    }

}
