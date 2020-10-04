package br.ufba.jnose.core.testsmelldetector.testsmell;

public class MethodUsage {
    private String testMethodName, productionMethod, begin, end;

    public MethodUsage(String testMethod, String productionMethod, String begin, String end) {
        this.testMethodName = testMethod;
        this.productionMethod = productionMethod;
        this.end = end;
        this.begin = begin;
    }

    public String getProductionMethod() {
        return productionMethod;
    }
    public String getTestMethodName() {
        return testMethodName;
    }


    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    public void setProductionMethod(String productionMethod) {
        this.productionMethod = productionMethod;
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

