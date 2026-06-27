package io.github.arieslab.dtolocal

import io.github.arieslab.dto.TestClass
import io.github.arieslab.entities.Projeto
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.Link
import java.io.Serializable

class ProjetoDTO : Serializable {

    var paraProcessar: Boolean = true
    var processado: Boolean = false
    var processado2: Boolean = false
    var procentagem: Int = 0
    var resultado: MutableList<MutableList<String>>? = null
    var resultadoByTestSmells: MutableList<TestClass>? = null
    var listaCommits: MutableList<Commit>? = null
    var listaTags: MutableList<Commit>? = null
    var commits: Int = 0
    var mapResults: MutableMap<Int, MutableList<MutableList<String>>>? = null
    var coverageCsvPath: String? = null
    var repoGit: String? = null
    var optionSelected: String = ""

    @Transient
    var iconProcessado: WebMarkupContainer? = null

    @Transient
    var iconNaoProcessado: WebMarkupContainer? = null

    @Transient
    var progressProject: WebMarkupContainer? = null

    @Transient
    var lkResultado: Link<*>? = null

    @Transient
    var lkCharts: Link<*>? = null

    @Transient
    var lkResult1: Link<*>? = null

    @Transient
    var lkResult2: Link<*>? = null

    @Transient
    var lkResult3: Link<*>? = null

    @Transient
    var lkResult4: Link<*>? = null

    @Transient
    var lbPorcentagem: Label? = null

    @Transient
    var bugs: String = ""

    var name: String
        get() = projeto.name
        set(value) { projeto.name = value }

    var path: String
        get() = projeto.path
        set(value) { projeto.path = value }

    @Transient
    private var projeto: Projeto = Projeto()

    val processadoCompleto: Boolean
        get() = processado && processado2

    companion object {
        fun fromProjeto(proj: Projeto): ProjetoDTO {
            val dto = ProjetoDTO()
            dto.name = proj.name
            dto.path = proj.path
            dto.commits = 0
            return dto
        }

        fun fromProjeto(proj: Projeto, processado: Boolean, processado2: Boolean, procentagem: Int): ProjetoDTO {
            val dto = ProjetoDTO()
            dto.name = proj.name
            dto.path = proj.path
            dto.processado = processado
            dto.processado2 = processado2
            dto.procentagem = procentagem
            dto.commits = 0
            return dto
        }
    }
}
