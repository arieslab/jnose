package br.ufba.jnose.core.testfilemapping;

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

    public static List<String[]>  start(List<String[]> listTestFile, String projectPath, String projectName) throws IOException {

        List<TestFile> testFiles = new ArrayList<>();

        for(String[] linhaSplit:listTestFile) {

            String commitId = linhaSplit[0];
            String commitName = linhaSplit[1];
            String commitDate = linhaSplit[2];
            String commitMsg = linhaSplit[3];

            String pathFile = linhaSplit[4];
            int loc = Integer.parseInt(linhaSplit[5]);
            int qtdMethods = Integer.parseInt(linhaSplit[6]);

            System.out.println("Detecting: " + pathFile);

            MappingDetector mappingDetector = new MappingDetector();
            TestFile tf = mappingDetector.detectMapping(pathFile, projectPath);

            tf.setLoc(loc);
            tf.setMethodsSize(qtdMethods);
            tf.setCommitId(commitId);
            tf.setCommitName(commitName);
            tf.setCommitDate(commitDate);
            tf.setCommitMsg(commitMsg);
            testFiles.add(tf);
        }

        System.out.println("Saving results. Total lines:" + testFiles.size());

        List<String[]> listRetorno = new ArrayList<>();

        for (int i = 0; i < testFiles.size(); i++) {

            String[] linha = {
                    testFiles.get(i).getCommitId(),
                    testFiles.get(i).getCommitName(),
                    testFiles.get(i).getCommitDate(),
                    testFiles.get(i).getCommitMsg(),
                    projectName,
                    testFiles.get(i).getFilePath(),
                    testFiles.get(i).getProductionFilePath(),
                    testFiles.get(i).getLoc()+"",
                    testFiles.get(i).getMethodsSize()+""
            };

            listRetorno.add(linha);
        }

        return listRetorno;
    }

    public static String start(String pathFileCSV, String projectPath, String projectName ,String reportPath) throws IOException {

        LOGGER.info("pathFileCSV: " + pathFileCSV + " - projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);

        File selectedFile = new File(pathFileCSV);
        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);

        List<TestFile> testFiles = new ArrayList<>();

        String pathFile;
        while ((pathFile = in.readLine()) != null) {
            int loc = Integer.parseInt(pathFile.split(",")[1]);
            int qtdMethods = Integer.parseInt(pathFile.split(",")[2]);
            pathFile = pathFile.split(",")[0];
            System.out.println("Detecting: " + pathFile);
            MappingDetector mappingDetector = new MappingDetector();
            TestFile tf = mappingDetector.detectMapping(pathFile, projectPath);
            tf.setLoc(loc);
            tf.setMethodsSize(qtdMethods);
            testFiles.add(tf);
        }

        System.out.println("Saving results. Total lines:" + testFiles.size());
        String outFile = reportPath + projectName + "_testmappingdetector" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        List<String> columnValues = null;

        for (int i = 0; i < testFiles.size(); i++) {
            columnValues = new ArrayList<>();
            columnValues.add(0, projectName);
            columnValues.add(1, testFiles.get(i).getFilePath());
            columnValues.add(2, testFiles.get(i).getProductionFilePath());
            columnValues.add(3, testFiles.get(i).getLoc()+"");
            columnValues.add(4, testFiles.get(i).getMethodsSize()+"");
            if(!testFiles.get(i).getProductionFilePath().isEmpty())
            resultsWriter.writeLine(columnValues);
        }

        System.out.println("Completed!");
        in.close();
        fileReader.close();
//        new File(pathFileCSV).deleteOnExit();
        return resultsWriter.getOutputFile();
    }
}
