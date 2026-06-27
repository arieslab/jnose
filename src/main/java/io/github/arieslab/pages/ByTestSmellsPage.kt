package io.github.arieslab.pages

import io.github.arieslab.WicketApplication
import io.github.arieslab.base.JNose
import io.github.arieslab.base.Util
import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.dtolocal.TotalProcessado
import io.github.arieslab.entities.Projeto
import io.github.arieslab.pages.base.BasePage
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import org.apache.wicket.spring.injection.annot.SpringBean
import java.io.File
import java.time.Duration
import java.util.*

class ByTestSmellsPage : BasePage("ByTestSmellsPage") {

    @SpringBean
    private lateinit var projetoBusiness: ProjetoBusiness

    private var pastaPath = ""
    private var pathAppToWebapp = BasePage.resolveRealPath()
    private var pastaPathReport = "$pathAppToWebapp${File.separatorChar}reports${File.separatorChar}"
    private lateinit var lbPastaSelecionada: Label
    private val listaProjetos = mutableListOf<ProjetoDTO>()
    private val indicator = AjaxIndicatorAppender()
    private lateinit var lvProjetos: ListView<ProjetoDTO>
    private val totalProcessado = TotalProcessado()
    private val totalProgressBar = HashMap<Int, Int>()
    private var processando = false
    private lateinit var processarTodos: IndicatingAjaxLink<String>
    private lateinit var lbProjetosSize: Label
    private var dataProcessamentoAtual = Util.dateNowFolder()
    private val logRetorno = StringBuffer()
    private val listaResultado = mutableListOf<MutableList<String>>()
    private lateinit var lkResultadoBotton: Link<String>

    init {
        lbProjetosSize = Label("lbProjetosSize", Model.of("0")).apply {
            setOutputMarkupPlaceholderTag(true)
            outputMarkupId = true
        }
        add(lbProjetosSize)

        criarListaProjetos()
        criarTimer()

        add(FeedbackPanel("feedback").apply { outputMarkupId = true })

        criarBotaoProcessarTodos()

        lbPastaSelecionada = Label("lbPastaSelecionada", pastaPath)
        add(lbPastaSelecionada)

        lkResultadoBotton = object : Link<String>("lkResultado") {
            override fun onClick() {
                setResponsePage(ResultPage(listaResultado, "Result By TestSmells", "result_bytestsmells", false))
            }
        }.apply {
            isEnabled = processando
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(lkResultadoBotton)

        loadProjetos()
    }

    private fun criarBotaoProcessarTodos() {
        processarTodos = object : IndicatingAjaxLink<String>("processarTodos") {
            override fun onClick(target: AjaxRequestTarget) {
                lbPastaSelecionada.defaultModel = Model.of(pastaPath)
                processando = true
                val listaParaProcessar = listaProjetos.filter { it.paraProcessar }

                JNose.processarProjetos(listaParaProcessar, dataProcessamentoAtual, pastaPathReport, totalProcessado)

                for (projeto in listaProjetos) {
                    @Suppress("UNCHECKED_CAST")
                    (projeto.resultado as? MutableList<MutableList<String>>)?.let { listaResultado.addAll(it) }
                }
            }
        }.apply { isEnabled = false }
        add(processarTodos)
    }

    private fun criarListaProjetos() {
        lvProjetos = object : ListView<ProjetoDTO>("lvProjetos", listaProjetos) {
            override fun populateItem(item: ListItem<ProjetoDTO>) {
                val projeto = item.modelObject

                val lkResultado = object : Link<String>("lkResultado") {
                    override fun onClick() {
                        setResponsePage(ResultPage2(projeto, projeto.resultado!!, "Result By TestSmells: ${projeto.name}", "${projeto.name}_result_bytestsmells", false))
                    }
                }.apply {
                    isEnabled = projeto.processado
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                item.add(lkResultado)
                projeto.lkResultado = lkResultado

                item.add(object : AjaxCheckBox("paraProcessarACB", object : IModel<Boolean> {
                    override fun getObject() = projeto.paraProcessar
                    override fun setObject(value: Boolean) { projeto.paraProcessar = value }
                }) {
                    override fun onUpdate(target: AjaxRequestTarget) {
                        val listaProjetosProcessar = listaProjetos.filter { it.paraProcessar }

                        processarTodos.isEnabled = listaProjetosProcessar.isNotEmpty()
                        target.add(processarTodos)

                        lbProjetosSize.defaultModel = Model.of(listaProjetosProcessar.size)
                        target.add(lbProjetosSize)
                    }
                })

                item.add(Label("nomeProjeto", projeto.name))
                item.add(Label("projeto", projeto.path))

                val progressProject = WebMarkupContainer("progressProject").apply {
                    setOutputMarkupPlaceholderTag(true)
                    outputMarkupId = true
                    add(AttributeModifier("style", "width: ${projeto.procentagem}%"))
                }
                item.add(progressProject)
                projeto.progressProject = progressProject

                val lbPorcetagem = Label("lbPorcentagem", projeto.procentagem).apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                projeto.lbPorcentagem = lbPorcetagem
                progressProject.add(lbPorcetagem)
            }
        }.apply {
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(lvProjetos)
    }

    private fun loadProjetos() {
        dataProcessamentoAtual = Util.dateNowFolder()
        totalProcessado.setValor(0)
        lbPastaSelecionada.defaultModel = Model.of(WicketApplication.JNOSE_PROJECTS_FOLDER)
        val listProjectBean = projetoBusiness.listAllWithFilter()
        for (projeto in listProjectBean) {
            listaProjetos.add(ProjetoDTO.fromProjeto(projeto))
        }
        lvProjetos.list = listaProjetos
        processarTodos.isEnabled = true
        lbProjetosSize.defaultModel = Model.of(listaProjetos.size)
    }

    private fun criarTimer() {
        add(object : AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            override fun onTimer(target: AjaxRequestTarget) {
                var todosProjetosProcessados = true
                val listaProjetosProcessar = listaProjetos.filter { it.paraProcessar }

                for (projeto in listaProjetosProcessar) {
                    lkResultadoBotton.isEnabled = projeto.processado
                    target.add(lkResultadoBotton)

                    projeto.lkResultado?.isEnabled = projeto.processado
                    projeto.lkResultado?.let { target.add(it) }

                    val lbPorcentagem = projeto.lbPorcentagem
                    lbPorcentagem?.defaultModel = Model.of(projeto.procentagem)
                    lbPorcentagem?.let { target.add(it) }

                    val progressProject = projeto.progressProject
                    progressProject?.add(AttributeModifier("style", "width: ${projeto.procentagem}%"))
                    progressProject?.let { target.add(it) }

                    todosProjetosProcessados = todosProjetosProcessados && projeto.processado
                }

                if (todosProjetosProcessados) {
                    totalProcessado.setValor((100 - totalProcessado.valor) + totalProcessado.valor)
                    processando = false
                }
            }
        })
    }
}
