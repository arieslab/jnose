package io.github.arieslab.base;

import org.apache.wicket.protocol.http.WebApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVCore {

    private static final Logger LOGGER = Logger.getLogger(CSVCore.class.getName());

    private static String outputFile;
    private static FileWriter writer;

    private static String pathAppToWebapp;
    private static String reportPath;

    /**
     * Initializes the CSV core with the servlet context path for report storage.
     *
     * @param webApplication the Wicket web application
     */
    public static void load(WebApplication webApplication){
        pathAppToWebapp = webApplication.getServletContext().getRealPath("");
        reportPath = pathAppToWebapp + File.separatorChar + "reports" + File.separatorChar;
    }

    /**
     * Creates a CSV file for test mapping detector results.
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder
     * @param nomeProjeto the project name
     * @return the output file path
     */
    public static String criarTestmappingdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testmappingdetector");
    }

    /**
     * Creates a CSV file for test smell detector results.
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder
     * @param nomeProjeto the project name
     * @return the output file path
     */
    public static String criarTestSmellsdetectorCSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testsmesll");
    }

    /**
     * Creates a CSV file for coverage data.
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder
     * @return the output file path
     */
    public static String criarCoberturaCSV(List<List<String>> todasLinhas, String pastaDataHora){
        return criarCSV(todasLinhas,pastaDataHora,"ClassInfor");
    }

    /**
     * Creates a CSV file for evolution data (table 1).
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder
     * @param nomeProjeto the project name
     * @return the output file path
     */
    public static String criarEvolution1CSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto + "_testsmell_evolution1");
    }

    /**
     * Creates a CSV file for evolution data (table 2).
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder
     * @param nomeProjeto the project name
     * @return the output file path
     */
    public static String criarEvolution2CSV(List<List<String>> todasLinhas, String pastaDataHora, String nomeProjeto){
        return criarCSV(todasLinhas,pastaDataHora,nomeProjeto +"_testsmell_evolution2");
    }

    /**
     * Writes a list of data rows to a CSV file.
     *
     * @param todasLinhas the data rows
     * @param pastaDataHora the timestamp folder name
     * @param nomeArquivo the output file name (without extension)
     * @return the absolute path to the created CSV file
     */
    public static String criarCSV(List<List<String>> todasLinhas,String pastaDataHora, String nomeArquivo){
        String outFolder = reportPath + pastaDataHora;
        String outFile = reportPath + pastaDataHora + File.separatorChar + nomeArquivo + ".csv";

        new File(outFolder).mkdirs();

        loadResultsWrite(outFile);

        for (List<String> linha : todasLinhas){
            writeLine(linha);
        }

        return getOutputFile();
    }

    /**
     * Initializes the CSV file for writing.
     *
     * @param outputFile the path to the output file
     */
    private static void loadResultsWrite(String outputFile){
        CSVCore.outputFile = outputFile;
        try {
            new File(outputFile).createNewFile();
            writer = new FileWriter(outputFile,false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create CSV file: " + outputFile, e);
        }
    }

    /**
     * Returns the current output file path.
     *
     * @return the output file path
     */
    private static String getOutputFile() {
        return outputFile;
    }

    /**
     * Appends a single data row to the CSV file using semicolon as delimiter.
     *
     * @param dataValues the row values
     */
    private static void writeLine(List<String> dataValues) {
        try {
            writer = new FileWriter(outputFile, true);

            for (int i = 0; i < dataValues.size(); i++) {
                if(dataValues.get(i) == null){
                    dataValues.set(i,"");
                }
                writer.append(dataValues.get(i).replace("\n", "").replace("\r", ""));

                if (i != dataValues.size() - 1)
                    writer.append(";");
                else
                    writer.append(System.lineSeparator());

            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Failed to write CSV line", e);
        }
    }

}
