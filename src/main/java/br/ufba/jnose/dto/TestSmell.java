package br.ufba.jnose.dto;

public class TestSmell{
    public String name;
    public String method;
    public String lineNumber;
    public String begin;
    public String end;

    @Override
    public String toString() {
        return "TestSmell{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", lineNumber=" + lineNumber +
                ", range='[" + begin + "-" + end + "]" +'\'' +
                '}';
    }
}
