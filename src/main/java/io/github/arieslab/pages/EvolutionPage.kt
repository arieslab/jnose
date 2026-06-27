package io.github.arieslab.pages

import io.github.arieslab.base.GitCore
import io.github.arieslab.base.JNose
import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.entities.Projeto
import io.github.arieslab.pages.base.BasePage
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxPreventSubmitBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.Radio
import org.apache.wicket.markup.html.form.RadioGroup
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.LambdaModel
import org.apache.wicket.model.Model
import org.apache.wicket.spring.injection.annot.SpringBean
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.logging.Logger

class EvolutionPage : BasePage("EvolutionPage") {

    companion object {
        private val LOGGER = Logger.getLogger(EvolutionPage::class.java.name)
    }

    @SpringBean
    private lateinit var projetoBusiness: ProjetoBusiness

    private lateinit var taLogInfo: Label
    private val logRetorno = StringBuffer()
    private val listaProjetos = mutableListOf<ProjetoDTO>()
    private lateinit var lvProjetos: ListView<ProjetoDTO>
    private val evolutionExecutor = Executors.newSingleThreadExecutor()

    init {
        criarTimer()
        criarListaProjetos()
        criarLogInfo()
        loadProjetos()
    }

    private fun loadProjetos() {
        val listProjetoBean = projetoBusiness.listAllWithFilter()
        for (projeto in listProjetoBean) {
            listaProjetos.add(ProjetoDTO.fromProjeto(projeto))
        }
        lvProjetos.list = listaProjetos
    }

    private fun criarLogInfo() {
        taLogInfo = Label("taLogInfo", Model.of(logRetorno)).apply {
            setEscapeModelStrings(false)
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(taLogInfo)
    }

    private fun criarTimer() {
        add(object : AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            override fun onTimer(target: AjaxRequestTarget) {
                taLogInfo.defaultModelObject = logRetorno
                target.add(taLogInfo)

                for (projeto in listaProjetos) {
                    projeto.mapResults?.let { map ->
                        projeto.lkResult1?.apply {
                            isEnabled = map.containsKey(1)
                            if (isEnabled) add(AttributeModifier.remove("style"))
                            else add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                            target.add(this)
                        }
                        projeto.lkResult2?.apply {
                            isEnabled = map.containsKey(2)
                            if (isEnabled) add(AttributeModifier.remove("style"))
                            else add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                            target.add(this)
                        }
                        projeto.lkResult3?.apply {
                            isEnabled = map.containsKey(3)
                            if (isEnabled) add(AttributeModifier.remove("style"))
                            else add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                            target.add(this)
                        }
                        projeto.lkResult4?.apply {
                            isEnabled = map.containsKey(4)
                            if (isEnabled) add(AttributeModifier.remove("style"))
                            else add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                            target.add(this)
                        }
                    }
                }
            }
        })
    }

    private fun criarListaProjetos() {
        lvProjetos = object : ListView<ProjetoDTO>("lvProjetos", listaProjetos) {
            override fun populateItem(item: ListItem<ProjetoDTO>) {
                val projeto = item.modelObject

                val mapResults = ConcurrentHashMap<Int, MutableList<MutableList<String>>>()
                projeto.mapResults = mapResults

                item.add(Label("nomeProjeto", projeto.name))
                item.add(Label("path", projeto.path))

                if (Files.exists(Path.of(projeto.path))) {
                    item.add(Label("branch", GitCore.branch(projeto.path)))
                    projeto.listaCommits = GitCore.gitLogOneLine(projeto.path)
                    projeto.listaTags = GitCore.gitTags(projeto.path)
                } else {
                    item.add(Label("branch", "Diretório não encontrado"))
                    projeto.listaCommits = mutableListOf()
                    projeto.listaTags = mutableListOf()
                }

                val form = Form<String>("form").apply {
                    outputMarkupId = true
                    add(AjaxPreventSubmitBehavior())
                }

                val lkResult1 = object : Link<String>("lkResult1") {
                    override fun onClick() {
                        val todasLinhas1 = mapResults[1]
                        setResponsePage(ResultPage(todasLinhas1!!, "Evolution Report 1 - TestSmells by Commit and Class: ${projeto.name}", "resultado_evolution1", false))
                    }
                }.apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                    isEnabled = false
                    add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                }
                projeto.lkResult1 = lkResult1
                form.add(lkResult1)

                val lkResult2 = object : Link<String>("lkResult2") {
                    override fun onClick() {
                        val todasLinhas2 = mapResults[2]
                        setResponsePage(ResultPage(todasLinhas2!!, "Evolution Report 2 - Total Testsmells by Commit: ${projeto.name}", "resultado_evolution2", false))
                    }
                }.apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                    isEnabled = false
                    add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                }
                projeto.lkResult2 = lkResult2
                form.add(lkResult2)

                val lkResult3 = object : Link<String>("lkResult3") {
                    override fun onClick() {
                        val todasLinhas3 = mapResults[3]
                        setResponsePage(ResultPage(todasLinhas3!!, "Evolution Report 3 - Testsmells Detail by Commit: ${projeto.name}", "resultado_evolution3", false))
                    }
                }.apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                    isEnabled = false
                    add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                }
                projeto.lkResult3 = lkResult3
                form.add(lkResult3)

                val lkResult4 = object : Link<String>("lkResult4") {
                    override fun onClick() {
                        val todasLinhas4 = mapResults[4]
                        setResponsePage(ResultPage(todasLinhas4!!, "Evolution Report 4 - Unique Testsmells: ${projeto.name}", "resultado_evolution4", false))
                    }
                }.apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                    isEnabled = false
                    add(AttributeModifier.append("style", "background-color: #e0e0eb;"))
                }
                projeto.lkResult4 = lkResult4
                form.add(lkResult4)

                form.add(object : AjaxLink<String>("btSubmit") {
                    override fun onClick(target: AjaxRequestTarget) {
                        LOGGER.info("Processamento do projeto: ${projeto.name} - Start")
                        logRetorno.insert(0, "Processamento do projeto: ${projeto.name} - Start<br>")
                        evolutionExecutor.submit {
                            JNose.processarEvolution(projeto, logRetorno, projeto.mapResults!!)
                        }
                    }
                }.apply { isEnabled = false })

                val radioCommitsTags = RadioGroup("radioCommitsTags", LambdaModel.of({ projeto.optionSelected }, { v -> projeto.optionSelected = v })).apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }

                radioCommitsTags.add(Radio("commit", Model("commit")).apply {
                    add(object : AjaxEventBehavior("change") {
                        override fun onEvent(target: AjaxRequestTarget) {
                            projeto.optionSelected = "commit"
                            form.get("btSubmit")?.isEnabled = true
                            target.add(form.get("btSubmit"))
                        }
                    })
                })
                radioCommitsTags.add(Label("ck1", Model(projeto.listaCommits?.size ?: 0)))

                radioCommitsTags.add(Radio("tag", Model("tag")).apply {
                    add(object : AjaxEventBehavior("change") {
                        override fun onEvent(target: AjaxRequestTarget) {
                            projeto.optionSelected = "tag"
                            form.get("btSubmit")?.isEnabled = true
                            target.add(form.get("btSubmit"))
                        }
                    })
                })
                radioCommitsTags.add(Label("ck2", Model(projeto.listaTags?.size ?: 0)))

                form.add(radioCommitsTags)
                item.add(form)
            }
        }.apply {
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(lvProjetos)
    }
}
