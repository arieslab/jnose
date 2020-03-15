package br.ufba.jnose.core.cobertura;

import br.ufba.jnose.util.ResultsWriter;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        start(null);
    }

    public static void start(String csvPath) throws Exception {
        File selectedFile = new File(csvPath);

        BufferedReader in = new BufferedReader(new FileReader(selectedFile));
        String str;

        String[] lineItem;

        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter("ClassInfor.csv");
        resultsWriter.writeLine(Arrays.asList("name","id","instructions","branches","lines","methods","complexity"));

        while ((str = in.readLine()) != null) {
            lineItem = str.split(",");
            ClassInfo classInfo = new ClassInfo(System.out);
            String file = lineItem[1].replace("/src/test/java/", "/target/test-classes/").replace(".java", ".class");
            String[] linha = classInfo.execute(file);
            resultsWriter.writeLine(Arrays.asList(linha));

//            CoreTutorial coreTutorial = new CoreTutorial(System.out);
//            coreTutorial.execute(lista[0]);
        }
        System.out.println("Completed!");

    }
}