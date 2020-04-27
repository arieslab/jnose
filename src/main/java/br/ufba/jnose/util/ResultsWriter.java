package br.ufba.jnose.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ResultsWriter {

    public String getOutputFile() {
        return outputFile;
    }

    private String outputFile;
    private FileWriter writer;

    private ResultsWriter(String outputFile) {
        this.outputFile = outputFile;
        try {
            writer = new FileWriter(outputFile,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResultsWriter createResultsWriter(String outputFile) {
        return new ResultsWriter(outputFile);
    }

    public void writeColumnName(List<String> columnNames) {
        writeOutput(columnNames);
    }

    public void writeLine(List<String> columnValues) {
        writeOutput(columnValues);
    }

    private void writeOutput(List<String> dataValues) {
        try {
            writer = new FileWriter(outputFile, true);

            for (int i = 0; i < dataValues.size(); i++) {
                writer.append(String.valueOf(dataValues.get(i)));

                if (i != dataValues.size() - 1)
                    writer.append(",");
                else
                    writer.append(System.lineSeparator());

            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
