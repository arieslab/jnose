package br.ufba.jnose.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.io.Serializable;

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private Boolean paraProcessar;
    private Boolean processado;
    private Boolean processado2;
    private Integer procentagem;

    public WebMarkupContainer iconProcessado;
    public WebMarkupContainer iconNaoProcessado;

    public WebMarkupContainer progressProject;

    public Label lbPorcentagem;
    public String bugs;

    public Projeto(String name, String path) {
        this.name = name;
        this.path = path;
        this.processado = false;
        this.processado2 = false;
        this.procentagem = 0;
        this.paraProcessar = true;
    }

    public Projeto(String name, String path, Boolean processado, Boolean processado2, Integer procentagem) {
        this.name = name;
        this.path = path;
        this.processado = processado;
        this.processado2 = processado2;
        this.procentagem = procentagem;
        this.paraProcessar = true;
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
}
