package br.ufba.jnose.pages.base;

import br.ufba.jnose.pages.*;
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

        Link linkProjetos = new Link<String>("linkProjetos") {
            @Override
            public void onClick() {
                setResponsePage(ProjetosPage.class);
            }
        };

        Link linkByClassTest = new Link<String>("linkByClassTest") {
            @Override
            public void onClick() {
                setResponsePage(ByClassTestPage.class);
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

        Link linkStorage = new Link<String>("linkStorage") {
            @Override
            public void onClick() {
                setResponsePage(StoragePage.class);
            }
        };

        add(linkProjetos);
        add(linkByClassTest);
        add(linkEvolution);
        add(linkConfig);
        add(linkByTestSmells);
        add(linkStorage);

        AbstractAjaxTimerBehavior timerHome = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            String signal = "";
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                if(signal.isEmpty()){
                    signal = "*";
                    footTime.setDefaultModel(Model.of(signal));
                }else{
                    signal = "";
                    footTime.setDefaultModel(Model.of(signal));
                }
                target.add(footTime);
            }
        };
        add(timerHome);

    }

}
