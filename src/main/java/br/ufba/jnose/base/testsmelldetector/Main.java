package br.ufba.jnose.base.testsmelldetector;

import br.ufba.jnose.WicketApplication;
//import br.ufba.jnose.core.ResultsWriter;
import br.ufba.jnose.base.CSVCore;
import br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.base.testsmelldetector.testsmell.TestFile;
import br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static List<String> columnNames;

    public static Map<String, String[]> jacocoMap;

    public static List<String[]> start(List<String[]> listMapping, Boolean cabecalho){

        br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector testSmellDetector = br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector.createTestSmellDetector();

        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile testFile;

        List<br.ufba.jnose.base.testsmelldetector.testsmell.TestFile> testFiles = new ArrayList<>();
        for(String[] lineItem:listMapping) {

            if(lineItem[3] == null){
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0],lineItem[1],lineItem[2],lineItem[3],lineItem[4], lineItem[5],lineItem[6], "",0,0);
            }
            else{
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0],lineItem[1],lineItem[2],lineItem[3],lineItem[4],lineItem[5], lineItem[6], lineItem[7], Integer.parseInt(lineItem[8]),Integer.parseInt(lineItem[9]));
            }
            testFiles.add(testFile);
        }

        List<String[]> listTestSmells = new ArrayList<>();

        if(cabecalho) {
            List<String> listaNameTestSmells = testSmellDetector.getTestSmellNames();
            String[] arrayNameTestSmells = listaNameTestSmells.toArray(new String[listaNameTestSmells.size()]);
            String[] linhaColumnNames = {"CommitID","CommitName","CommitDate","CommitMsg","Tag", "App", "TestFileName", "ProductionFileName", "LOC", "numberMethods"};
            String[] linhaColumn = Stream.concat(Arrays.stream(linhaColumnNames), Arrays.stream(arrayNameTestSmells)).toArray(String[]::new);
            listTestSmells.add(linhaColumn);
        }


        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile tempFile;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;

        for (br.ufba.jnose.base.testsmelldetector.testsmell.TestFile file : testFiles) {
            try {
                date = new Date();
                System.out.println(dateFormat.format(date) + " Processing: " + file.getTestFilePath());
                System.out.println("Processing: " + file.getTestFilePath());

                tempFile = testSmellDetector.detectSmells(file);

                List<String> columnValues = new ArrayList<>();

                //dados da classe
                columnValues.add(file.getCommitId());
                columnValues.add(file.getCommitName());
                columnValues.add(file.getCommitDate());
                columnValues.add(file.getCommitMsg());
                columnValues.add(file.getTag());

                columnValues.add(file.getApp());
                columnValues.add(file.getTestFileName().replace(".java", ""));
                String targetFile = file.getProductionFileName().replace(".java", "");
                columnValues.add(targetFile);

                //LOC
                columnValues.add(file.getLoc().toString());

                //NUMBER METHODS
                columnValues.add(file.getQtdMethods().toString());

                //add test smells
                for (br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell smell : tempFile.getTestSmells()) {
//                    if(smell != null) {//verificar porque esta vindo smells nulls
//                        smell.getSmellyElements();
                    try {
                        columnValues.add(String.valueOf(smell.getSmellyElements().stream().filter(x -> x.getHasSmell()).count()));
                    } catch (NullPointerException e) {
                        columnValues.add("");
                    }
//                    }
                }

                String[] linha = columnValues.toArray(new String[columnValues.size()]);

                listTestSmells.add(linha);

            }catch (Error e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return listTestSmells;
    }

    public static String start(String csvPath, String projectName, String reportPath, String pastaDataHora) throws IOException {

        File selectedFile = new File(csvPath);
        br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector testSmellDetector = br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector.createTestSmellDetector();

        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);
        String str;

        String[] lineItem;
        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile testFile;

        List<br.ufba.jnose.base.testsmelldetector.testsmell.TestFile> testFiles = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            System.out.println(str);
            lineItem = str.split(",");
            if(lineItem[2] == null){
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0], lineItem[1], "",0,0);
            }
            else{
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0], lineItem[1], lineItem[2], Integer.parseInt(lineItem[3]),Integer.parseInt(lineItem[4]));
            }
            testFiles.add(testFile);
        }

        List<List<String>> todasLinhas = new ArrayList<>();

        List<String> columnValues;

        //Coluna name testsmells
        columnNames = testSmellDetector.getTestSmellNames();

        //add colunas de descrição anterior a lista dos testsmells
        columnNames.add(0, "App");
        columnNames.add(1, "TestFileName");
        columnNames.add(2, "ProductionFileName");
        columnNames.add(3,"LOC");
        columnNames.add(4,"numberMethods");

        //jacoco - lista de dados de cobertura posterior
        if(WicketApplication.COBERTURA_ON) {
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
        }
        todasLinhas.add(columnNames);

        if(WicketApplication.COBERTURA_ON) {
            jacocoMap = jacocoProcess(projectName, reportPath);
        }

        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile tempFile;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        
        for (br.ufba.jnose.base.testsmelldetector.testsmell.TestFile file : testFiles) {
            try {
                date = new Date();
                System.out.println(dateFormat.format(date) + " Processing: " + file.getTestFilePath());
                System.out.println("Processing: " + file.getTestFilePath());

                tempFile = testSmellDetector.detectSmells(file);

                columnValues = new ArrayList<>();

                //dados da classe
                columnValues.add(file.getApp());
                columnValues.add(file.getTestFileName().replace(".java", ""));
                String targetFile = file.getProductionFileName().replace(".java", "");
                columnValues.add(targetFile);

                //LOC
                columnValues.add(file.getLoc().toString());

                //NUMBER METHODS
                columnValues.add(file.getQtdMethods().toString());

                //add test smells
                for (br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell smell : tempFile.getTestSmells()) {
                    smell.getSmellyElements();
                    try {
                        columnValues.add(String.valueOf(smell.getSmellyElements().stream().filter(x -> x.getHasSmell()).count()));
                    } catch (NullPointerException e) {
                        columnValues.add("");
                    }
                }

                if(WicketApplication.COBERTURA_ON) {
                    jacocoEscreverArquivo(columnValues, file, targetFile);
                }
                todasLinhas.add(columnValues);
            }catch (Error e){
                e.printStackTrace();
                System.out.println("Continuando a o processo!");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Continuando a o processo!");
            }

        }

        return CSVCore.criarTestSmellsdetectorCSV(todasLinhas,pastaDataHora,projectName);
    }

    public static List<List<String>> start2(String csvPath, String projectName, String reportPath, String pastaDataHora) throws IOException {

        File selectedFile = new File(csvPath);
        br.ufba.jnose.base.testsmelldetector.testsmell.TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);
        String str;

        String[] lineItem;
        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile testFile;

        List<br.ufba.jnose.base.testsmelldetector.testsmell.TestFile> testFiles = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            System.out.println(str);
            lineItem = str.split(",");
            if(lineItem[2] == null){
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0], lineItem[1], "",0,0);
            }
            else{
                testFile = new br.ufba.jnose.base.testsmelldetector.testsmell.TestFile(lineItem[0], lineItem[1], lineItem[2], Integer.parseInt(lineItem[3]),Integer.parseInt(lineItem[4]));
            }
            testFiles.add(testFile);
        }

        List<List<String>> todasLinhas = new ArrayList<>();

        List<String> columnValues;

        //Coluna name testsmells
        columnNames = testSmellDetector.getTestSmellNames();

        //add colunas de descrição anterior a lista dos testsmells
        columnNames.add(0, "App");
        columnNames.add(1, "TestFileName");
        columnNames.add(2, "ProductionFileName");
        columnNames.add(3,"LOC");
        columnNames.add(4,"numberMethods");

        //jacoco - lista de dados de cobertura posterior
        if(WicketApplication.COBERTURA_ON) {
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
        }
        todasLinhas.add(columnNames);

        if(WicketApplication.COBERTURA_ON) {
            jacocoMap = jacocoProcess(projectName, reportPath);
        }

        br.ufba.jnose.base.testsmelldetector.testsmell.TestFile tempFile;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;

        for (br.ufba.jnose.base.testsmelldetector.testsmell.TestFile file : testFiles) {
            try {
                date = new Date();
                System.out.println(dateFormat.format(date) + " Processing: " + file.getTestFilePath());
                System.out.println("Processing: " + file.getTestFilePath());

                tempFile = testSmellDetector.detectSmells(file);

                columnValues = new ArrayList<>();

                //dados da classe
                columnValues.add(file.getApp());
                columnValues.add(file.getTestFileName().replace(".java", ""));
                String targetFile = file.getProductionFileName().replace(".java", "");
                columnValues.add(targetFile);

                //LOC
                columnValues.add(file.getLoc().toString());

                //NUMBER METHODS
                columnValues.add(file.getQtdMethods().toString());

                //add test smells
                for (AbstractSmell smell : tempFile.getTestSmells()) {
                    smell.getSmellyElements();
                    try {
                        columnValues.add(String.valueOf(smell.getSmellyElements().stream().filter(x -> x.getHasSmell()).count()));
                    } catch (NullPointerException e) {
                        columnValues.add("");
                    }
                }

                if(WicketApplication.COBERTURA_ON) {
                    jacocoEscreverArquivo(columnValues, file, targetFile);
                }
                todasLinhas.add(columnValues);
            }catch (Error e){
                e.printStackTrace();
                System.out.println("Continuando a o processo!");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Continuando a o processo!");
            }

        }

        return todasLinhas;
    }

    private static void jacocoEscreverArquivo(List<String> columnValues, TestFile file, String targetFile) {
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
    }

    private static Map<String, String[]> jacocoProcess(String projectName, String reportPath) throws IOException {
        String str;
        File jacocoFile = new File(reportPath + projectName+"_jacoco.csv");
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
        return jacocoMap;
    }


}
