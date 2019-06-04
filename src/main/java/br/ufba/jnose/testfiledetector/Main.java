package br.ufba.jnose.testfiledetector;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufba.jnose.util.ResultsWriter;
import br.ufba.jnose.testfiledetector.entity.ClassEntity;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static String start(String projectPath, String projectName, String reportPath) throws IOException {

        LOGGER.info("projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);

        FileWalker fw = new FileWalker();
        List<Path> files = fw.getJavaTestFiles(projectPath, true);
        TestFileDetector testFileDetector = TestFileDetector.createTestFileDetector();

        String outFile = reportPath + projectName + "_testfiledetection" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        for (Path file : files) {
            try {
                ClassEntity classEntity = testFileDetector.runAnalysis(file);
                List<String> list = new ArrayList<String>();
                list.add(classEntity.getFilePath() + "," + getLineCount(classEntity));
                resultsWriter.writeLine(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultsWriter.getOutputFile();
    }

    private static int getLineCount(ClassEntity classEntity) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(classEntity.getFilePath()));
        int lineNumber = 0;
        int data = lineNumberReader.read();
        while (data != -1) {
            data = lineNumberReader.read();
            lineNumber = lineNumberReader.getLineNumber();
        }
        lineNumberReader.close();
        return lineNumber;
    }
}
