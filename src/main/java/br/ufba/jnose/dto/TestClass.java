package br.ufba.jnose.dto;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestClass implements Serializable {
    private static final long serialVersionUID = 1L;

    public String projectName;
    public Path pathFile;
    public String name;
    public Integer numberMethods;
    public Integer numberLine;
    public String productionFile;
    public List<TestSmell> listTestSmell = new ArrayList<>();
    public JunitVersion junitVersion;

    public enum JunitVersion{JUnit3, JUnit4, JUnit5}

    @Override
    public String toString() {
        return "TestClass{" +
                "projectName=" + projectName +
                "pathFile=" + pathFile +
                ", name='" + name + '\'' +
                ", numberMethods=" + numberMethods +
                ", numberLine=" + numberLine +
                ", junitVersion='" + junitVersion + '\'' +
                ", productionFile='" + productionFile + '\'' +
                ", listTestSmell=" + listTestSmell +
                '}';
    }
}
