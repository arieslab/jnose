package io.github.arieslab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String path;

    private String url;

    private Integer stars;

    private String junitVersion;

    private Date dateUpdate;

    @OneToMany
    private List<TestSmell> testSmell;

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
     * Returns the project name.
     *
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the project name.
     *
     * @param name the project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the local filesystem path of the project.
     *
     * @return the project path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the local filesystem path of the project.
     *
     * @param path the project path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the remote repository URL.
     *
     * @return the repository URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the remote repository URL.
     *
     * @param url the repository URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the star count from the remote repository.
     *
     * @return the star count
     */
    public Integer getStars() {
        return stars;
    }

    /**
     * Sets the star count.
     *
     * @param stars the star count
     */
    public void setStars(Integer stars) {
        this.stars = stars;
    }

    /**
     * Returns the detected JUnit version.
     *
     * @return the JUnit version string
     */
    public String getJunitVersion() {
        return junitVersion;
    }

    /**
     * Sets the JUnit version.
     *
     * @param junitVersion the JUnit version string
     */
    public void setJunitVersion(String junitVersion) {
        this.junitVersion = junitVersion;
    }

    /**
     * Returns the list of associated test smells.
     *
     * @return list of test smells
     */
    public List<TestSmell> getTestSmell() {
        return testSmell;
    }

    /**
     * Sets the list of associated test smells.
     *
     * @param testSmell list of test smells
     */
    public void setTestSmell(List<TestSmell> testSmell) {
        this.testSmell = testSmell;
    }

    /**
     * Returns the date of the last update.
     *
     * @return the last update date
     */
    public Date getDateUpdate() {
        return dateUpdate;
    }

    /**
     * Sets the date of the last update.
     *
     * @param dateUpdate the last update date
     */
    public void setDateUpdate(Date dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return id.equals(projeto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", stars=" + stars +
                ", junitVersion='" + junitVersion + '\'' +
                '}';
    }
}
