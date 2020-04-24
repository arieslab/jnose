package br.ufba.jnose.core.testfilemapping;

import br.ufba.jnose.core.JNoseUtils;
import br.ufba.jnose.core.evolution.Commit;
import br.ufba.jnose.util.ResultsWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(br.ufba.jnose.core.testfiledetector.Main.class.getName());

    public static List<String[]>  start(List<JNoseUtils.TestClass> listTestClass, Commit commit, String projectPath, String projectName) throws IOException {
        System.out.println("Saving results. Total lines:" + listTestClass.size());
        List<String[]> listRetorno = new ArrayList<>();
        for (JNoseUtils.TestClass testClass: listTestClass) {
            String[] linha = {
                    commit.id,
                    commit.name,
                    commit.date.toString(),
                    commit.msg,
                    projectName,
                    testClass.pathFile.toString(),
                    testClass.productionFile,
                    testClass.numberLine+"",
                    testClass.numberMethods+""
            };
            listRetorno.add(linha);
        }
        return listRetorno;
    }

    public static String start(List<JNoseUtils.TestClass> listTestClass, String pathFileCSV, String projectPath, String projectName, String reportPath) throws IOException {
        LOGGER.info("pathFileCSV: " + pathFileCSV + " - projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);
        File selectedFile = new File(pathFileCSV);
        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);
        System.out.println("Saving results. Total lines:" + listTestClass.size());
        String outFile = reportPath + projectName + "_testmappingdetector" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);
        List<String> columnValues = null;
        for (JNoseUtils.TestClass testClass: listTestClass){
            columnValues = new ArrayList<>();
            columnValues.add(0, projectName);
            columnValues.add(1, testClass.pathFile.toString());
            columnValues.add(2, testClass.productionFile);
            columnValues.add(3, testClass.numberLine+"");
            columnValues.add(4, testClass.numberMethods+"");
            resultsWriter.writeLine(columnValues);
        }
        System.out.println("Completed!");
        in.close();
        fileReader.close();
        return resultsWriter.getOutputFile();
    }
}
