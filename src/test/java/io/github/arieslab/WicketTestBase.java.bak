package io.github.arieslab;

import de.agilecoders.wicket.core.Bootstrap;
import io.github.arieslab.business.ProjetoBusiness;
import io.github.arieslab.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;

import static org.mockito.Mockito.mock;

public class WicketTestBase {

    protected static ApplicationContextMock mockCtx;
    protected static WicketTester tester;

    @BeforeAll
    public static void setUp() {
        mockCtx = new ApplicationContextMock();
        mockCtx.putBean(mock(ProjetoBusiness.class));

        String basePath = new File("src/main/webapp").getAbsolutePath();
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends WebPage> getHomePage() {
                return HomePage.class;
            }

            @Override
            protected void init() {
                super.init();
                Bootstrap.install(this);
                getComponentInstantiationListeners().add(
                        new SpringComponentInjector(this, mockCtx, false));
                getMarkupSettings().setStripWicketTags(false);
                mountPage("/projects", io.github.arieslab.pages.ProjetosPage.class);
                mountPage("/byclasstest", io.github.arieslab.pages.ByClassTestPage.class);
                mountPage("/bytestsmells", io.github.arieslab.pages.ByTestSmellsPage.class);
                mountPage("/evolution", io.github.arieslab.pages.EvolutionPage.class);
                mountPage("/config", io.github.arieslab.pages.ConfigPage.class);
                mountPage("/research", io.github.arieslab.pages.ResearchPage.class);
            }
        }, basePath);
    }

    @AfterAll
    public static void tearDown() {
        if (tester != null) {
            tester.destroy();
        }
    }
}
