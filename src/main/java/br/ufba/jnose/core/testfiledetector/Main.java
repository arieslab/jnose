package br.ufba.jnose.core.testfiledetector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.ufba.jnose.core.JNoseUtils;
import br.ufba.jnose.core.evolution.Commit;
import br.ufba.jnose.core.testfiledetector.entity.ClassEntity;
import br.ufba.jnose.util.ResultsWriter;

public class Main {

    public static List<String[]> start(String projectPath, Commit commit) {

//        FileWalker fw = new FileWalker();
//        List<Path> files = null;
//        try {
//            files = fw.getJavaTestFiles(projectPath, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        TestFileDetector testFileDetector = TestFileDetector.createTestFileDetector();


        List<JNoseUtils.TestClass> files = null;
        try {
            files = JNoseUtils.getFilesTest(projectPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String[]> retorno = new ArrayList<>();

        for (JNoseUtils.TestClass testClass : files) {
//            ClassEntity classEntity = null;
//            try {
//                classEntity = testFileDetector.runAnalysis(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
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

//        FileWalker fw = new FileWalker();
//        List<Path> files = fw.getJavaTestFiles(projectPath, true);

        List<JNoseUtils.TestClass> files = JNoseUtils.getFilesTest(projectPath);

//        TestFileDetector testFileDetector = TestFileDetector.createTestFileDetector();

        String outFile = reportPath + projectName + "_testfiledetection" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        for (JNoseUtils.TestClass testClass : files) {
            try {

//                ClassEntity classEntity = testFileDetector.runAnalysis(file);
                List<String> list = new ArrayList<String>();
//                list.add(classEntity.getFilePath() + "," + getLineCount(classEntity) + "," + classEntity.getMethods().size() + "");

                list.add(testClass.pathFile + "," + testClass.numberLine + "," + testClass.numberMethods + "");

//                resultsWriter.writeLine(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultsWriter.getOutputFile();
    }

//    private static int getLineCount(ClassEntity classEntity) {
//        int lineNumber = 0;
//        LineNumberReader lineNumberReader = null;
//        try {
//            lineNumberReader = new LineNumberReader(new FileReader(classEntity.getFilePath()));
//            int data = lineNumberReader.read();
//            while (data != -1) {
//                data = lineNumberReader.read();
//                lineNumber = lineNumberReader.getLineNumber();
//            }
//            lineNumberReader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return lineNumber;
//    }
}
