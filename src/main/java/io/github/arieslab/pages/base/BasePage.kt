package io.github.arieslab.pages.base

import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.pages.*
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.spring.injection.annot.SpringBean
import java.time.Duration

open class BasePage(paginaAtual: String) : WebPage() {

    @SpringBean
    private lateinit var projetoBusiness: ProjetoBusiness

    private val footTime: WebMarkupContainer = WebMarkupContainer("footTimeHome")

    init {
        footTime.outputMarkupId = true
        footTime.setOutputMarkupPlaceholderTag(true)
        add(footTime)

        add(linkPara("linkProjetos", ProjetosPage::class.java, paginaAtual))
        add(linkPara("linkByClassTest", ByClassTestPage::class.java, paginaAtual))
        add(linkPara("linkByTestSmells", ByTestSmellsPage::class.java, paginaAtual))
        add(linkPara("linkEvolution", EvolutionPage::class.java, paginaAtual))
        add(linkPara("linkAnalyze", AnalyzePage::class.java, paginaAtual))
        add(linkPara("linkConfig", ConfigPage::class.java, paginaAtual))
        add(linkPara("linkResearch", ResearchPage::class.java, paginaAtual))

        add(object : AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            override fun onTimer(target: AjaxRequestTarget) {
                footTime.isVisible = !footTime.isVisible
                target.add(footTime)
            }
        })
    }

    companion object {
        private fun linkPara(id: String, pageClass: Class<out WebPage>, paginaAtual: String): Link<Void> {
            val pageSimpleName = pageClass.simpleName
            val link = object : Link<Void>(id) {
                override fun onClick() {
                    setResponsePage(pageClass)
                }
            }
            if (paginaAtual == pageSimpleName) {
                link.add(AttributeModifier("style", "color:red"))
            }
            return link
        }

        @JvmStatic
        protected fun resolveRealPath(): String {
            return try {
                val app = WebApplication.get()
                val realPath = app.servletContext.getRealPath("")
                if (realPath != null) realPath else System.getProperty("user.dir")
            } catch (_: Exception) {
                System.getProperty("user.dir")
            }
        }
    }
}
