package br.ufba.jnose.dto;

import java.io.Serializable;

public class TestSmell implements Serializable {
    private static final long serialVersionUID = 1L;

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
