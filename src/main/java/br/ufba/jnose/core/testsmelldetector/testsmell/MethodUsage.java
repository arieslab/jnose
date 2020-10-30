package br.ufba.jnose.core.testsmelldetector.testsmell;

public class MethodUsage {
    private String testMethodName, productionMethodName, begin, end;

    public MethodUsage(String testMethod, String productionMethod, String begin, String end) {
        this.testMethodName = testMethod;
        this.productionMethodName = productionMethod;
        this.end = end;
        this.begin = begin;
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

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

