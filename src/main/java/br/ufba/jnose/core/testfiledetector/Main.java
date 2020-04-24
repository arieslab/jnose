package br.ufba.jnose.core.testfiledetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufba.jnose.core.JNoseUtils;
import br.ufba.jnose.util.ResultsWriter;

public class Main {

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

}
