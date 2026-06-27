package io.github.arieslab.base;

import org.apache.wicket.protocol.http.WebApplication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVCore {

    private static final Logger LOGGER = Logger.getLogger(CSVCore.class.getName());

    private static String outputFile;
    private static BufferedWriter writer;

    private static String pathAppToWebapp;
    private static String reportPath;

    public static void load(WebApplication webApplication){
        var realPath = webApplication.getServletContext().getRealPath("");
        if (realPath != null) {
            pathAppToWebapp = realPath;
        } else {
            pathAppToWebapp = System.getProperty("user.dir");
        }
        reportPath = pathAppToWebapp + "/reports/";
    }

    public static String criarTestmappingdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testmappingdetector");
    }

    public static String criarTestSmellsdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testsmesll");
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

    public static String criarCSV(List<List<String>> todasLinhas,String pastaDataHora, String nomeArquivo){
        String outFolder = reportPath + pastaDataHora;
        String outFile = reportPath + pastaDataHora + "/" + nomeArquivo + ".csv";

        try {
            Files.createDirectories(Path.of(outFolder));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create directory: " + outFolder, e);
        }

        loadResultsWrite(outFile);

        for (List<String> linha : todasLinhas){
            writeLine(linha);
        }

        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close CSV file", e);
        }

        return getOutputFile();
    }

    private static void loadResultsWrite(String outputFile){
        CSVCore.outputFile = outputFile;
        try {
            Files.createDirectories(Path.of(outputFile).getParent());
            writer = Files.newBufferedWriter(Path.of(outputFile), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create CSV file: " + outputFile, e);
        }
    }

    private static String getOutputFile() {
        return outputFile;
    }

    private static void writeLine(List<String> dataValues) {
        try {
            for (int i = 0; i < dataValues.size(); i++) {
                if(dataValues.get(i) == null){
                    dataValues.set(i, "");
                }
                writer.write(dataValues.get(i).replace("\n", "").replace("\r", ""));

                if (i != dataValues.size() - 1)
                    writer.write(";");
                else
                    writer.write(System.lineSeparator());
            }
            writer.flush();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Failed to write CSV line", e);
        }
    }

}
