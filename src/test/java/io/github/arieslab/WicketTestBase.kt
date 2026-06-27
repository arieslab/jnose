package io.github.arieslab

import de.agilecoders.wicket.core.Bootstrap
import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.pages.HomePage
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import org.apache.wicket.spring.test.ApplicationContextMock
import org.apache.wicket.util.tester.WicketTester
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.mockito.Mockito
import java.io.File

open class WicketTestBase {

    companion object {
        @JvmStatic
        protected lateinit var mockCtx: ApplicationContextMock
        @JvmStatic
        protected lateinit var tester: WicketTester

        @JvmStatic
        @BeforeAll
        fun setUp() {
            mockCtx = ApplicationContextMock()
            mockCtx.putBean(Mockito.mock(ProjetoBusiness::class.java))

            val basePath = File("src/main/webapp").absolutePath
            tester = WicketTester(object : WebApplication() {
                override fun getHomePage(): Class<out WebPage> = HomePage::class.java

                override fun init() {
                    super.init()
                    Bootstrap.install(this)
                    componentInstantiationListeners.add(SpringComponentInjector(this, mockCtx, false))
                    markupSettings.stripWicketTags = false
                    mountPage("/projects", io.github.arieslab.pages.ProjetosPage::class.java)
                    mountPage("/byclasstest", io.github.arieslab.pages.ByClassTestPage::class.java)
                    mountPage("/bytestsmells", io.github.arieslab.pages.ByTestSmellsPage::class.java)
                    mountPage("/evolution", io.github.arieslab.pages.EvolutionPage::class.java)
                    mountPage("/config", io.github.arieslab.pages.ConfigPage::class.java)
                    mountPage("/research", io.github.arieslab.pages.ResearchPage::class.java)
                }
            }, basePath)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            if (::tester.isInitialized) {
                tester.destroy()
            }
        }
    }
}
