package br.ufba.jnose.testfilemapping;

import br.ufba.jnose.util.ResultsWriter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(br.ufba.jnose.testfiledetector.Main.class.getName());

    public static String start(String pathFileCSV, String projectPath, String projectName ,String reportPath) throws IOException {

        LOGGER.info("pathFileCSV: " + pathFileCSV + " - projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);

        File selectedFile = new File(pathFileCSV);
        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);

        List<TestFile> testFiles = new ArrayList<>();

        String pathFile;
        while ((pathFile = in.readLine()) != null) {
            int loc = Integer.parseInt(pathFile.split(",")[1]);
            pathFile = pathFile.split(",")[0];
            System.out.println("Detecting: " + pathFile);
            MappingDetector mappingDetector = new MappingDetector();
            TestFile tf = mappingDetector.detectMapping(pathFile, projectPath);
            tf.setLoc(loc);
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
