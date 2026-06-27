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
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.LambdaModel
import org.apache.wicket.model.Model
import org.apache.wicket.spring.injection.annot.SpringBean
import java.io.File
import java.time.Duration

class ByClassTestPage : BasePage("ByClassTestPage") {

    @SpringBean
    private lateinit var projetoBusiness: ProjetoBusiness

    private var pastaPath = ""
    private var pathAppToWebapp = BasePage.resolveRealPath()
    private var pastaPathReport = "$pathAppToWebapp${File.separatorChar}reports${File.separatorChar}"
    private val listaProjetos = mutableListOf<ProjetoDTO>()
    private lateinit var lvProjetos: ListView<ProjetoDTO>
    private lateinit var taLog: Label
    private val totalProcessado = TotalProcessado()
    private var processando = false
    private lateinit var processarTodos: IndicatingAjaxLink<String>
    private val logRetorno = StringBuffer()
    private var dataProcessamentoAtual = Util.dateNowFolder()
    private var processarCobertura = false
    private val listaResultado = mutableListOf<MutableList<String>>()
    private lateinit var lkResultadoBotton: Link<String>

    init {
        totalProcessado.setValor(0)

        criarCheckBoxCobertura()

        taLog = Label("taLog").apply {
            setEscapeModelStrings(false)
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(taLog)

        criarListaProjetos()

        add(FeedbackPanel("feedback").apply { outputMarkupId = true })

        criarBotaoProcessarTodos()

        lkResultadoBotton = object : Link<String>("lkResultado") {
            override fun onClick() {
                setResponsePage(ResultPage(listaResultado, "Result By ClassTest", "result_byclasstest", true))
            }
        }.apply {
            isEnabled = processando
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(lkResultadoBotton)

        add(object : Link<String>("lkCharts") {
            override fun onClick() {}
        }.apply { isVisible = false })

        loadProjetos()
        criarTimer()
    }

    private fun criarBotaoProcessarTodos() {
        processarTodos = object : IndicatingAjaxLink<String>("processarTodos") {
            override fun onClick(target: AjaxRequestTarget) {
                processando = true
                val listaParaProcessar = listaProjetos.filter { it.paraProcessar }
                val result = JNose.processarProjetos2(listaParaProcessar, dataProcessamentoAtual, totalProcessado, pastaPathReport, logRetorno)
                listaResultado.addAll(result)
            }
        }.apply { isEnabled = false }
        add(processarTodos)
    }

    private fun criarCheckBoxCobertura() {
        val acbCobertura = object : AjaxCheckBox("acbCobertura", Model.of(processarCobertura)) {
            override fun onUpdate(target: AjaxRequestTarget) {
                processarCobertura = !processarCobertura
                WicketApplication.COBERTURA_ON = processarCobertura
                println("COVERAGE_ON: $processarCobertura")
                logRetorno.insert(0, "COVERAGE_ON: $processarCobertura <br>")
            }
        }
        add(acbCobertura)
    }

    private fun loadProjetos() {
        dataProcessamentoAtual = Util.dateNowFolder()
        logRetorno.setLength(0)
        totalProcessado.setValor(0)
        val listaProjetosBean = projetoBusiness.listAllWithFilter()
        for (projeto in listaProjetosBean) {
            listaProjetos.add(ProjetoDTO.fromProjeto(projeto))
        }
        lvProjetos.list = listaProjetos
        processarTodos.isEnabled = true
    }

    private fun criarListaProjetos() {
        lvProjetos = object : ListView<ProjetoDTO>("lvProjetos", listaProjetos) {
            override fun populateItem(item: ListItem<ProjetoDTO>) {
                val projetoDTO = item.modelObject

                val lkCharts = object : Link<String>("lkCharts") {
                    override fun onClick() {}
                }.apply {
                    isEnabled = projetoDTO.processado
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                item.add(lkCharts)
                projetoDTO.lkCharts = lkCharts

                val lkResultado = object : Link<String>("lkResultado") {
                    override fun onClick() {
                        setResponsePage(ResultPage(projetoDTO.resultado!!, "Result By ClassTest: ${projetoDTO.name}", "${projetoDTO.name}_result_byclasstest", true, projetoDTO.coverageCsvPath))
                    }
                }.apply {
                    isEnabled = projetoDTO.processado
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                item.add(lkResultado)
                projetoDTO.lkResultado = lkResultado

                item.add(object : AjaxCheckBox("paraProcessarACB", Model.of(projetoDTO.paraProcessar)) {
                    override fun onUpdate(target: AjaxRequestTarget) {
                        projetoDTO.paraProcessar = !projetoDTO.paraProcessar
                        val listaProjetosProcessar = listaProjetos.filter { it.paraProcessar }
                        processarTodos.isEnabled = listaProjetosProcessar.isNotEmpty()
                        target.add(processarTodos)
                    }
                })

                item.add(Label("nomeProjeto", projetoDTO.name))
                item.add(Label("projeto", projetoDTO.path))

                val iconProcessado = WebMarkupContainer("iconProcessado").apply {
                    isVisible = projetoDTO.processado
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                item.add(iconProcessado)
                projetoDTO.iconProcessado = iconProcessado

                val iconNaoProcessado = WebMarkupContainer("iconNaoProcessado").apply {
                    isVisible = !projetoDTO.processado
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                item.add(iconNaoProcessado)
                projetoDTO.iconNaoProcessado = iconNaoProcessado

                val progressProject = WebMarkupContainer("progressProject").apply {
                    setOutputMarkupPlaceholderTag(true)
                    outputMarkupId = true
                    add(AttributeModifier("style", "width: ${projetoDTO.procentagem}%"))
                }
                item.add(progressProject)
                projetoDTO.progressProject = progressProject

                val lbPorcetagem = Label("lbPorcentagem", projetoDTO.procentagem).apply {
                    outputMarkupId = true
                    setOutputMarkupPlaceholderTag(true)
                }
                projetoDTO.lbPorcentagem = lbPorcetagem
                progressProject.add(lbPorcetagem)
            }
        }.apply {
            outputMarkupId = true
            setOutputMarkupPlaceholderTag(true)
        }
        add(lvProjetos)
    }

    private fun criarTimer() {
        add(object : AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            override fun onTimer(target: AjaxRequestTarget) {
                taLog.defaultModel = Model.of(logRetorno.toString())
                target.add(taLog)

                var todosProjetosProcessados = true
                val listaProjetosProcessar = listaProjetos.filter { it.paraProcessar }

                for (projetoDTO in listaProjetosProcessar) {
                    lkResultadoBotton.apply {
                        if (!isEnabled) {
                            isEnabled = projetoDTO.processado
                            target.add(this)
                        }
                    }

                    projetoDTO.lkResultado?.apply {
                        if (!isEnabled) {
                            isEnabled = projetoDTO.processado
                            target.add(this)
                        }
                    }

                    projetoDTO.lkCharts?.apply {
                        if (!isEnabled) {
                            isEnabled = projetoDTO.processado
                            target.add(this)
                        }
                    }

                    projetoDTO.iconProcessado?.apply {
                        isVisible = projetoDTO.processado
                        target.add(this)
                    }

                    projetoDTO.iconNaoProcessado?.apply {
                        isVisible = !projetoDTO.processado
                        target.add(this)
                    }

                    projetoDTO.lbPorcentagem?.apply {
                        defaultModel = Model.of(projetoDTO.procentagem)
                        target.add(this)
                    }

                    projetoDTO.progressProject?.apply {
                        add(AttributeModifier("style", "width: ${projetoDTO.procentagem}%"))
                        target.add(this)
                    }

                    todosProjetosProcessados = todosProjetosProcessados && projetoDTO.processado
                }

                if (todosProjetosProcessados) {
                    totalProcessado.setValor((100 - totalProcessado.valor) + totalProcessado.valor)
                    processando = false
                }
            }
        })
    }
}
