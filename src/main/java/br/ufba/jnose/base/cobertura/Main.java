package br.ufba.jnose.base.cobertura;

import br.ufba.jnose.base.CSVCore;
import br.ufba.jnose.base.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        start(null);
    }

    public static void start(String csvPath) throws Exception {
        File selectedFile = new File(csvPath);

        BufferedReader in = new BufferedReader(new FileReader(selectedFile));
        String str;

        String[] lineItem;

        List<List<String>> todasLinhas = new ArrayList<>();

        todasLinhas.add(Arrays.asList("name","id","instructions","branches","lines","methods","complexity"));

        while ((str = in.readLine()) != null) {
            lineItem = str.split(",");
            ClassInfo classInfo = new ClassInfo(System.out);
            String file = lineItem[1].replace("/src/test/java/", "/target/test-classes/").replace(".java", ".class");
            String[] linha = classInfo.execute(file);
            todasLinhas.add(Arrays.asList(linha));

        }
        System.out.println("Completed!");

        CSVCore.criarCoberturaCSV(todasLinhas, Util.dateNowFolder());

    }
}