package br.ufba.jnose.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.io.Serializable;

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private Boolean processado;
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
        this.procentagem = 0;
    }

    public Projeto(String name, String path, Boolean processado, Integer procentagem) {
        this.name = name;
        this.path = path;
        this.processado = processado;
        this.procentagem = procentagem;
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
        return processado;
    }

    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    public Integer getProcentagem() {
        return procentagem;
    }

    public void setProcentagem(Integer procentagem) {
        this.procentagem = procentagem;
    }
}
