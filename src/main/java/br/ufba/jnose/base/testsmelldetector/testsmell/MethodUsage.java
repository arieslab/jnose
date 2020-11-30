package br.ufba.jnose.base.testsmelldetector.testsmell;

import java.util.ArrayList;

public class MethodUsage {
    private String testMethodName, productionMethodName, begin, end;
    private ArrayList<String> lines;

    public MethodUsage(String testMethod, String productionMethod, String begin, String end) {
        this.testMethodName = testMethod;
        this.productionMethodName = productionMethod;
        this.end = end;
        this.begin = begin;
    }

    public MethodUsage(String testMethod, ArrayList<String> lines){
        this.lines = lines;
        this.testMethodName = testMethod;
    }

    public MethodUsage (String testMethod, String productionMethod, ArrayList<String> lines) {
        this.lines = lines;
        this.testMethodName = testMethod;
        this.productionMethodName = productionMethod;
    }

    public String getRange () {
        return String.join(", ", lines);
    }

    public String getProductionMethodName() {
        return productionMethodName;
    }
    public String getTestMethodName() {
        return testMethodName;
    }


    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    public void setProductionMethodName(String productionMethodName) {
        this.productionMethodName = productionMethodName;
    }

    public String getLine() {
        return begin;
    }
    public String getBlock() {
        return begin.concat(" - ").concat(end);
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getREMOVEEEE() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

