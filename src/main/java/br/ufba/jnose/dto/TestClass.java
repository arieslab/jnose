package br.ufba.jnose.dto;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestClass implements Serializable {
    private static final long serialVersionUID = 1L;

    public Path pathFile;
    public String name;
    public Integer numberMethods;
    public Integer numberLine;
    public String productionFile;
    public List<TestSmell> listTestSmell = new ArrayList<>();

    @Override
    public String toString() {
        return "TestClass{" +
                "pathFile=" + pathFile +
                ", name='" + name + '\'' +
                ", numberMethods=" + numberMethods +
                ", numberLine=" + numberLine +
                ", productionFile='" + productionFile + '\'' +
                ", listTestSmell=" + listTestSmell +
                '}';
    }
}
