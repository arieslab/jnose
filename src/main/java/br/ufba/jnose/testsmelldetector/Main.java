package br.ufba.jnose.testsmelldetector;

import br.ufba.jnose.util.ResultsWriter;
import br.ufba.jnose.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.testsmelldetector.testsmell.TestFile;
import br.ufba.jnose.testsmelldetector.testsmell.TestSmellDetector;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {

    public static List<String> columnNames;

    public static void start(String csvPath, Boolean window, String reportPath, String projectName) throws IOException {
        br.ufba.jnose.util.Main.textAreaLogProgram.textArea.setForeground(Color.YELLOW);

        File selectedFile = new File(csvPath);
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);
        String str;

        String[] lineItem;
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            System.out.println(str);
            lineItem = str.split(",");
            if(lineItem[2] == null){
                testFile = new TestFile(lineItem[0], lineItem[1], "",0);
            }
            else{
                testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2], Integer.parseInt(lineItem[3]));
            }
            testFiles.add(testFile);
        }

        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(reportPath+projectName+"_testsmesll.csv");
        List<String> columnValues;
        columnNames = testSmellDetector.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestFileName");
        columnNames.add(2, "ProductionFileName");
        columnNames.add("LOC");
        //jacoco
        columnNames.add("INSTRUCTION_MISSED");
        columnNames.add("INSTRUCTION_COVERED");
        columnNames.add("BRANCH_MISSED");
        columnNames.add("BRANCH_COVERED");
        columnNames.add("LINE_MISSED");
        columnNames.add("LINE_COVERED");
        columnNames.add("COMPLEXITY_MISSED");
        columnNames.add("COMPLEXITY_COVERED");
        columnNames.add("METHOD_MISSED");
        columnNames.add("METHOD_COVERED");
        resultsWriter.writeColumnName(columnNames);

        File jacocoFile = new File(reportPath+"/"+projectName+"_jacoco.csv");
        FileReader jacocoFileReader = new FileReader(jacocoFile);
        BufferedReader jacocoIn = new BufferedReader(jacocoFileReader);

        Map<String,String[]> jacocoMap = new HashMap<>();

        boolean pularLinha = false;
        while ((str = jacocoIn.readLine()) != null) {
            if(pularLinha) {
                String[] linha = str.split(",");
                jacocoMap.put(linha[2], linha);
            }else{
                pularLinha = true;
            }
        }
        jacocoIn.close();
        jacocoFileReader.close();
        jacocoFile.deleteOnExit();

        TestFile tempFile;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        
        for (TestFile file : testFiles) {
            date = new Date();
            System.out.println(dateFormat.format(date) + " Processing: "+file.getTestFilePath());
            System.out.println("Processing: "+file.getTestFilePath());

            tempFile = testSmellDetector.detectSmells(file);

            columnValues = new ArrayList<>();
            columnValues.add(file.getApp());
            columnValues.add(file.getTestFileName().replace(".java",""));
            String targetFile = file.getProductionFileName().replace(".java","");
            columnValues.add(targetFile);
            for (AbstractSmell smell : tempFile.getTestSmells()) {
                smell.getSmellyElements();
                try {
                    columnValues.add(String.valueOf(smell.getSmellyElements().stream().filter(x -> x.getHasSmell()).count()));
                }
                catch (NullPointerException e){
                    columnValues.add("");
                }
            }
            columnValues.add(file.getLoc()+"");
            String[] jacocoLinha = jacocoMap.get(targetFile);
            if(jacocoLinha != null) {
                columnValues.add(jacocoLinha[3]);
                columnValues.add(jacocoLinha[4]);
                columnValues.add(jacocoLinha[5]);
                columnValues.add(jacocoLinha[6]);
                columnValues.add(jacocoLinha[7]);
                columnValues.add(jacocoLinha[8]);
                columnValues.add(jacocoLinha[9]);
                columnValues.add(jacocoLinha[10]);
                columnValues.add(jacocoLinha[11]);
                columnValues.add(jacocoLinha[12]);
            }
            resultsWriter.writeLine(columnValues);

        }

        in.close();
        fileReader.close();
//        new File(csvPath).deleteOnExit();
        System.out.println("Completed!");

        if(window) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Window.createAndShowGUI(resultsWriter.getOutputFile());
                }
            });
        }

    }


}
