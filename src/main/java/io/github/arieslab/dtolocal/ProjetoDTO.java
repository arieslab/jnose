package io.github.arieslab.dtolocal;

import io.github.arieslab.dto.TestClass;
import io.github.arieslab.entities.Projeto;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProjetoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    private Link lkResultado;
    public Link lkCharts;
    public Link lkResult1;
    public Link lkResult2;
    public Link lkResult3;
    public Link lkResult4;

    public Label lbPorcentagem;
    public String bugs;

    private String repoGit;

    private String optionSelected;

    private io.github.arieslab.entities.Projeto projeto;

    /**
     * Creates a DTO from a Projeto entity with default processing state.
     *
     * @param projeto the project entity
     */
    public ProjetoDTO(Projeto projeto) {
        if(this.projeto == null)this.projeto = new io.github.arieslab.entities.Projeto();
        this.projeto.setName(projeto.getName());
        this.projeto.setPath(projeto.getPath());
        this.processado = false;
        this.processado2 = false;
        this.procentagem = 0;
        this.paraProcessar = true;
        this.commits = 0;
        this.optionSelected = "";
    }

    /**
     * Creates a DTO from a Projeto entity with specific processing state.
     *
     * @param projeto the project entity
     * @param processado whether first processing stage is complete
     * @param processado2 whether second processing stage is complete
     * @param procentagem the processing percentage
     */
    public ProjetoDTO(Projeto projeto, Boolean processado, Boolean processado2, Integer procentagem) {
        if(this.projeto == null)this.projeto = new Projeto();
        this.projeto.setName(projeto.getName());
        this.projeto.setPath(projeto.getPath());
        this.processado = processado;
        this.processado2 = processado2;
        this.procentagem = procentagem;
        this.paraProcessar = true;
        this.commits = 0;
        this.optionSelected = "";
    }

    /**
     * Returns the selected analysis option (commit or tag).
     *
     * @return the selected option
     */
    public String getOptionSelected() {
        return optionSelected;
    }

    /**
     * Sets the selected analysis option (commit or tag).
     *
     * @param optionSelected the selected option
     */
    public void setOptionSelected(String optionSelected) {
        this.optionSelected = optionSelected;
    }

    /**
     * Returns the processing results in tabular format.
     *
     * @return list of result rows
     */
    public List<List<String>> getResultado() {
        return resultado;
    }

    /**
     * Sets the processing results.
     *
     * @param resultado list of result rows
     */
    public void setResultado(List<List<String>> resultado) {
        this.resultado = resultado;
    }

    /**
     * Returns the test-class-level results.
     *
     * @return list of TestClass results
     */
    public List<TestClass> getResultadoByTestSmells() {
        return resultadoByTestSmells;
    }

    /**
     * Sets the test-class-level results.
     *
     * @param resultadoByTestSmells list of TestClass results
     */
    public void setResultadoByTestSmells(List<TestClass> resultadoByTestSmells) {
        this.resultadoByTestSmells = resultadoByTestSmells;
    }

    /**
     * Returns the list of selected commits.
     *
     * @return list of commits
     */
    public List<Commit> getListaCommits() {
        return listaCommits;
    }

    /**
     * Sets the list of selected commits.
     *
     * @param listaCommits list of commits
     */
    public void setListaCommits(List<Commit> listaCommits) {
        this.listaCommits = listaCommits;
    }

    /**
     * Returns the commit count.
     *
     * @return the commit count
     */
    public Integer getCommits() {
        return commits;
    }

    /**
     * Sets the commit count.
     *
     * @param commits the commit count
     */
    public void setCommits(Integer commits) {
        this.commits = commits;
    }

    /**
     * Returns whether this project is marked for processing.
     *
     * @return true if marked for processing
     */
    public Boolean getParaProcessar() {
        return paraProcessar;
    }

    /**
     * Sets whether this project is marked for processing.
     *
     * @param paraProcessar true if marked for processing
     */
    public void setParaProcessar(Boolean paraProcessar) {
        this.paraProcessar = paraProcessar;
    }

    /**
     * Returns the project name.
     *
     * @return the project name
     */
    public String getName() {
        return projeto.getName();
    }

    /**
     * Sets the project name.
     *
     * @param name the project name
     */
    public void setName(String name) {
        this.projeto.setName(name);
    }

    /**
     * Returns the project filesystem path.
     *
     * @return the project path
     */
    public String getPath() {
        return projeto.getPath();
    }

    /**
     * Sets the project filesystem path.
     *
     * @param path the project path
     */
    public void setPath(String path) {
        this.projeto.setPath(path);
    }

    /**
     * Returns whether both processing stages are complete.
     *
     * @return true if fully processed
     */
    public Boolean getProcessado() {
        return processado && processado2;
    }

    /**
     * Sets whether the first processing stage is complete.
     *
     * @param processado the first stage status
     */
    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    /**
     * Sets whether the second processing stage is complete.
     *
     * @param processado2 the second stage status
     */
    public void setProcessado2(Boolean processado2) {
        this.processado2 = processado2;
    }

    /**
     * Returns the current processing percentage.
     *
     * @return the percentage value
     */
    public Integer getProcentagem() {
        return procentagem;
    }

    /**
     * Sets the current processing percentage.
     *
     * @param procentagem the percentage value
     */
    public void setProcentagem(Integer procentagem) {
        this.procentagem = procentagem;
    }

    /**
     * Returns the git repository URL.
     *
     * @return the repository URL
     */
    public String getRepoGit() {
        return repoGit;
    }

    /**
     * Sets the git repository URL.
     *
     * @param repoGit the repository URL
     */
    public void setRepoGit(String repoGit) {
        this.repoGit = repoGit;
    }

    /**
     * Returns the list of tags.
     *
     * @return list of tags as Commit objects
     */
    public List<Commit> getListaTags() {
        return listaTags;
    }

    /**
     * Sets the list of tags.
     *
     * @param listaTags list of tags as Commit objects
     */
    public void setListaTags(List<Commit> listaTags) {
        this.listaTags = listaTags;
    }

    /**
     * Returns the evolution analysis results map.
     *
     * @return map of result tables
     */
    public Map<Integer, List<List<String>>> getMapResults() {
        return mapResults;
    }

    /**
     * Sets the evolution analysis results map.
     *
     * @param mapResults map of result tables
     */
    public void setMapResults(Map<Integer, List<List<String>>> mapResults) {
        this.mapResults = mapResults;
    }

    /**
     * Returns the underlying Projeto entity.
     *
     * @return the Projeto entity
     */
    public io.github.arieslab.entities.Projeto getProjeto() {
        return projeto;
    }

    /**
     * Sets the underlying Projeto entity.
     *
     * @param projeto the Projeto entity
     */
    public void setProjeto(io.github.arieslab.entities.Projeto projeto) {
        this.projeto = projeto;
    }

    /**
     * Returns the result link component.
     *
     * @return the Link component
     */
    public Link getLkResultado() {
        return lkResultado;
    }

    /**
     * Sets the result link component.
     *
     * @param lkResultado the Link component
     */
    public void setLkResultado(Link lkResultado) {
        this.lkResultado = lkResultado;
    }
}
