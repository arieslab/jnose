package br.ufba.jnose.core.testfiledetector;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufba.jnose.core.JNoseUtils;
import br.ufba.jnose.core.evolution.Commit;
import br.ufba.jnose.util.ResultsWriter;

public class Main {

    public static List<String[]> start(String projectPath, Commit commit) {

        List<JNoseUtils.TestClass> files = null;
        try {
            files = JNoseUtils.getFilesTest(projectPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String[]> retorno = new ArrayList<>();

        for (JNoseUtils.TestClass testClass : files) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
            String dateString = dateFormat.format(commit.date);
            String[] linhaArray = {commit.id, commit.name, dateString, commit.msg, testClass.pathFile.toString(), testClass.numberLine.toString(), testClass.numberMethods.toString()};
            retorno.add(linhaArray);
        }
        return retorno;
    }

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static String start(String projectPath, String projectName, String reportPath) throws IOException {

        LOGGER.info("projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);

        List<JNoseUtils.TestClass> files = JNoseUtils.getFilesTest(projectPath);
        String outFile = reportPath + projectName + "_testfiledetection" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        for (JNoseUtils.TestClass testClass : files) {
            try {
                List<String> list = new ArrayList<String>();
                list.add(testClass.pathFile + "," + testClass.numberLine + "," + testClass.numberMethods + "");
                resultsWriter.writeLine(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultsWriter.getOutputFile();
    }

    public static List<JNoseUtils.TestClass> start2(String projectPath, String projectName, String reportPath) throws IOException {
        return JNoseUtils.getFilesTest(projectPath);
    }

}
