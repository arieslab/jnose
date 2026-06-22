package io.github.arieslab.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class TestSmell implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    private String nome;
    private String pathTestClass;
    private String pathProductionClass;
    private String method;
    private String begin;
    private String end;

    @ManyToOne
    private Projeto projeto;

    /**
     * Returns the unique identifier.
     *
     * @return the entity id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     *
     * @param id the entity id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the test smell name.
     *
     * @return the smell name
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the test smell name.
     *
     * @param nome the smell name
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Returns the path to the test class file.
     *
     * @return the test class path
     */
    public String getPathTestClass() {
        return pathTestClass;
    }

    /**
     * Sets the path to the test class file.
     *
     * @param pathTestClass the test class path
     */
    public void setPathTestClass(String pathTestClass) {
        this.pathTestClass = pathTestClass;
    }

    /**
     * Returns the path to the production class file.
     *
     * @return the production class path
     */
    public String getPathProductionClass() {
        return pathProductionClass;
    }

    /**
     * Sets the path to the production class file.
     *
     * @param pathProductionClass the production class path
     */
    public void setPathProductionClass(String pathProductionClass) {
        this.pathProductionClass = pathProductionClass;
    }

    /**
     * Returns the method name where the smell was detected.
     *
     * @return the method name
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method name where the smell was detected.
     *
     * @param method the method name
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns the start line of the smell occurrence.
     *
     * @return the start line
     */
    public String getBegin() {
        return begin;
    }

    /**
     * Sets the start line of the smell occurrence.
     *
     * @param begin the start line
     */
    public void setBegin(String begin) {
        this.begin = begin;
    }

    /**
     * Returns the end line of the smell occurrence.
     *
     * @return the end line
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the end line of the smell occurrence.
     *
     * @param end the end line
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Returns the parent project entity.
     *
     * @return the parent project
     */
    public Projeto getProjeto() {
        return projeto;
    }

    /**
     * Sets the parent project entity.
     *
     * @param projeto the parent project
     */
    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestSmell testSmell = (TestSmell) o;
        return id.equals(testSmell.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TestSmell{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", pathTestClass='" + pathTestClass + '\'' +
                ", pathProductionClass='" + pathProductionClass + '\'' +
                ", method='" + method + '\'' +
                ", begin='" + begin + '\'' +
                ", end='" + end + '\'' +
                ", projeto=" + projeto +
                '}';
    }
}
