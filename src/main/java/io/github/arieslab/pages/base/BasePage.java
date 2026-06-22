package io.github.arieslab.pages.base;

import io.github.arieslab.business.ProjetoBusiness;
import io.github.arieslab.pages.*;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.Duration;

public class BasePage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ProjetoBusiness projetoBusiness;

    private WebMarkupContainer footTime;

    public BasePage(String paginaAtual) {
        footTime = new WebMarkupContainer("footTimeHome");
        footTime.setOutputMarkupId(true);
        footTime.setOutputMarkupPlaceholderTag(true);
        add(footTime);

        add(linkPara("linkProjetos", ProjetosPage.class, paginaAtual));
        add(linkPara("linkByClassTest", ByClassTestPage.class, paginaAtual));
        add(linkPara("linkByTestSmells", ByTestSmellsPage.class, paginaAtual));
        add(linkPara("linkEvolution", EvolutionPage.class, paginaAtual));
        add(linkPara("linkAnalyze", AnalyzePage.class, paginaAtual));
        add(linkPara("linkConfig", ConfigPage.class, paginaAtual));
        add(linkPara("linkResearch", ResearchPage.class, paginaAtual));

        AbstractAjaxTimerBehavior timerHome = new AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            String signal = "";
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                footTime.setVisible(!footTime.isVisible());
                target.add(footTime);
            }
        };
        add(timerHome);
    }

    private static BookmarkablePageLink<Void> linkPara(String id, Class<? extends WebPage> pageClass, String paginaAtual) {
        String pageSimpleName = pageClass.getSimpleName();
        var link = new BookmarkablePageLink<Void>(id, pageClass);
        if (paginaAtual.equals(pageSimpleName)) {
            link.add(new AttributeModifier("style", "color:red"));
        }
        return link;
    }
}
