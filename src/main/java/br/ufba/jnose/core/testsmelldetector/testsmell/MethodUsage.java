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
}

