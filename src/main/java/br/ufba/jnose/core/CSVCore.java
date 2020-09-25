package br.ufba.jnose.core;

import org.apache.wicket.protocol.http.WebApplication;

import java.io.File;
import java.util.List;

public class CSVCore {

    private static String pathAppToWebapp;
    private static String reportPath;

    public static void load(WebApplication webApplication){
        pathAppToWebapp = webApplication.getServletContext().getRealPath("");
        reportPath = pathAppToWebapp + File.separatorChar + "reports" + File.separatorChar;
    }

    public static String criarByTestSmellsCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_report_by_testsmells");
    }

    public static String criarTestfiledetectionCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testfiledetection");
    }

    public static String criarTestmappingdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testmappingdetector");
    }

    public static String criarTestSmellsdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testsmesll");
    }

    public static String criarTodosProjetosCSV(List<List<String>> todasLinhas, String pastaDataHora){
        return criarCSV(todasLinhas,pastaDataHora,"todos_projetos_testsmesll");
    }

    public static String criarCoberturaCSV(List<List<String>> todasLinhas, String pastaDataHora){
        return criarCSV(todasLinhas,pastaDataHora,"ClassInfor");
    }

    public static String criarEvolution1CSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testsmell_evolution1");
    }

    public static String criarEvolution2CSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto +"_testsmell_evolution2");
    }

    private static String criarCSV(List<List<String>> todasLinhas,String pastaDataHora, String nomeArquivo){
        String outFile = reportPath + pastaDataHora + File.separatorChar + nomeArquivo + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        for (List<String> linha : todasLinhas){
            resultsWriter.writeLine(linha);
        }

        return resultsWriter.getOutputFile();
    }

}
