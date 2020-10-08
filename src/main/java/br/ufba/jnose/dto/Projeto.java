package br.ufba.jnose.dto;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private Boolean paraProcessar;
    private Boolean processado;
    private Boolean processado2;
    private Integer procentagem;

    private List<List<String>> resultado;

    private List<TestClass> resultadoByTestSmells;

    private List<Commit> listaCommits;

    private List<Commit> listaTags;

    private Integer commits;

    private Map<Integer, List<List<String>>> mapResults;

    public WebMarkupContainer iconProcessado;
    public WebMarkupContainer iconNaoProcessado;

    public WebMarkupContainer progressProject;

    public Link lkResultado;
    public Link lkCharts;
    public Link lkResult1;
    public Link lkResult2;

    public Label lbPorcentagem;
    public String bugs;

    private String repoGit;

    private String optionSelected;

    public Projeto(String name, String path) {
        this.name = name;
        this.path = path;
        this.processado = false;
        this.processado2 = false;
        this.procentagem = 0;
        this.paraProcessar = true;
        this.commits = 0;
        this.optionSelected = "";
    }

    public Projeto(String name, String path, Boolean processado, Boolean processado2, Integer procentagem) {
        this.name = name;
        this.path = path;
        this.processado = processado;
        this.processado2 = processado2;
        this.procentagem = procentagem;
        this.paraProcessar = true;
        this.commits = 0;
        this.optionSelected = "";
    }

    public String getOptionSelected() {
        return optionSelected;
    }

    public void setOptionSelected(String optionSelected) {
        this.optionSelected = optionSelected;
    }

    public List<List<String>> getResultado() {
        return resultado;
    }

    public void setResultado(List<List<String>> resultado) {
        this.resultado = resultado;
    }

    public List<TestClass> getResultadoByTestSmells() {
        return resultadoByTestSmells;
    }

    public void setResultadoByTestSmells(List<TestClass> resultadoByTestSmells) {
        this.resultadoByTestSmells = resultadoByTestSmells;
    }

    public List<Commit> getListaCommits() {
        return listaCommits;
    }

    public void setListaCommits(List<Commit> listaCommits) {
        this.listaCommits = listaCommits;
    }

    public Integer getCommits() {
        return commits;
    }

    public void setCommits(Integer commits) {
        this.commits = commits;
    }

    public Boolean getParaProcessar() {
        return paraProcessar;
    }

    public void setParaProcessar(Boolean paraProcessar) {
        this.paraProcessar = paraProcessar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getProcessado() {
        return processado && processado2;
    }

    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    public void setProcessado2(Boolean processado2) {
        this.processado2 = processado2;
    }

    public Integer getProcentagem() {
        return procentagem;
    }

    public void setProcentagem(Integer procentagem) {
        this.procentagem = procentagem;
    }

    public String getRepoGit() {
        return repoGit;
    }

    public void setRepoGit(String repoGit) {
        this.repoGit = repoGit;
    }

    public List<Commit> getListaTags() {
        return listaTags;
    }

    public void setListaTags(List<Commit> listaTags) {
        this.listaTags = listaTags;
    }

    public Map<Integer, List<List<String>>> getMapResults() {
        return mapResults;
    }

    public void setMapResults(Map<Integer, List<List<String>>> mapResults) {
        this.mapResults = mapResults;
    }
}
