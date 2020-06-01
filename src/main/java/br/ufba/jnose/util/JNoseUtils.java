package br.ufba.jnose.util;

import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.SmellyElement;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestFile;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.dto.TestSmell;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JNoseUtils {

    private final static Logger LOGGER = Logger.getLogger(JNoseUtils.class.getName());

    private static String directoryPath = "/home/tassio/Desenvolvimento/projetos4/";

    public static void main(String[] args) throws IOException {
        System.out.println("JNoseUtils");
        List<TestClass> list = getFilesTest(directoryPath);
        System.out.println("qtd lista: " + list.size());
        list.parallelStream().forEach(f -> System.out.println(f.toString()));
    }

    public static String testfiledetector(String projectPath, String projectName, String reportPath) throws IOException {

        LOGGER.info("projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);

        List<TestClass> files = JNoseUtils.getFilesTest(projectPath);
        String outFile = reportPath + projectName + "_testfiledetection" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);

        for (TestClass testClass : files) {
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

    public static List<String[]> testfilemapping(List<TestClass> listTestClass, Commit commit, String projectPath, String projectName) throws IOException {
        System.out.println("Saving results. Total lines:" + listTestClass.size());
        List<String[]> listRetorno = new ArrayList<>();
        for (TestClass testClass : listTestClass) {
            String[] linha = {
                    commit.id,
                    commit.name,
                    commit.date.toString(),
                    commit.msg,
                    commit.tag,
                    projectName,
                    testClass.pathFile.toString(),
                    testClass.productionFile,
                    testClass.numberLine + "",
                    testClass.numberMethods + ""
            };
            listRetorno.add(linha);
        }
        return listRetorno;
    }

    public static String testfilemapping(List<TestClass> listTestClass, String pathFileCSV, String projectPath, String projectName, String reportPath) throws IOException {
        LOGGER.info("pathFileCSV: " + pathFileCSV + " - projectPath: " + projectPath + " - projectName: " + projectName + " - reportPath: " + reportPath);
        File selectedFile = new File(pathFileCSV);
        FileReader fileReader = new FileReader(selectedFile);
        BufferedReader in = new BufferedReader(fileReader);
        System.out.println("Saving results. Total lines:" + listTestClass.size());
        String outFile = reportPath + projectName + "_testmappingdetector" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);
        List<String> columnValues = null;
        for (TestClass testClass : listTestClass) {
            columnValues = new ArrayList<>();
            columnValues.add(0, projectName);
            columnValues.add(1, testClass.pathFile.toString());
            columnValues.add(2, testClass.productionFile);
            columnValues.add(3, testClass.numberLine + "");
            columnValues.add(4, testClass.numberMethods + "");
            resultsWriter.writeLine(columnValues);
        }
        System.out.println("Completed!");
        in.close();
        fileReader.close();
        return resultsWriter.getOutputFile();
    }

    public static String newReport(List<TestClass> listTestClass, String reportPath) throws IOException {
        System.out.println("Saving results. Total lines:" + listTestClass.size());
        String outFile = reportPath + File.separator + "all" + "_report_by_testsmells" + ".csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(outFile);
        List<String> columnValues = null;

        columnValues = new ArrayList<>();
        columnValues.add(0, "projectName");
        columnValues.add(1, "name");
        columnValues.add(2, "pathFile");
        columnValues.add(3, "productionFile");
        columnValues.add(4, "junitVersion");
        columnValues.add(5, "loc");
        columnValues.add(6, "qtdMethods");
        columnValues.add(7, "testSmellName");
        columnValues.add(8, "testSmellMethod");
        columnValues.add(9, "testSmellLine");
        columnValues.add(10, "testSmellLineBegin");
        columnValues.add(11, "testSmellLineEnd");
        resultsWriter.writeLine(columnValues);

        for (TestClass testClass : listTestClass) {
            for (TestSmell testSmell : testClass.listTestSmell) {
                columnValues = new ArrayList<>();
                columnValues.add(0, testClass.projectName);
                columnValues.add(1, testClass.name);
                columnValues.add(2, testClass.pathFile.toString());
                columnValues.add(3, testClass.productionFile);
                columnValues.add(4, testClass.junitVersion.name());
                columnValues.add(5, testClass.numberLine.toString());
                columnValues.add(6, testClass.numberMethods.toString());
                columnValues.add(7, testSmell.name);
                columnValues.add(8, testSmell.method);
                columnValues.add(9, testSmell.lineNumber);
                columnValues.add(10, testSmell.begin);
                columnValues.add(11, testSmell.end);
                resultsWriter.writeLine(columnValues);
            }
        }
        System.out.println("Completed!");
        return resultsWriter.getOutputFile();
    }


    public static List<TestClass> getFilesTest(String directoryPath) throws IOException {
        String projectName = directoryPath.substring(directoryPath.lastIndexOf(File.separatorChar) + 1, directoryPath.length());
        List<TestClass> files = new ArrayList<>();
        Path startDir = Paths.get(directoryPath);
        Files.walk(startDir)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    if (filePath.getFileName().toString().lastIndexOf(".") != -1) {
                        String fileNameWithoutExtension = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().lastIndexOf(".")).toLowerCase();
                        if (filePath.toString().toLowerCase().endsWith(".java") && fileNameWithoutExtension.matches("^.*test\\d*$")) {
                            TestClass testClass = new TestClass();
                            testClass.projectName = projectName;
                            testClass.pathFile = filePath;
                            if (isTestFile(testClass)) {
                                System.out.println("TestClass Detect -> " + testClass.pathFile);
                                String productionFileName = "";
                                int index = testClass.name.toLowerCase().lastIndexOf("test");
                                if (index > 0) {
                                    productionFileName = testClass.name.substring(0, testClass.name.toLowerCase().lastIndexOf("test")) + ".java";
                                }
                                testClass.productionFile = getFileProduction(startDir.toString(), productionFileName);

                                if (!testClass.productionFile.isEmpty()) {
                                    getTestSmells(testClass);
                                    files.add(testClass);
                                }
                            }
                        }
                    }
                });
        return files;
    }

    private static boolean isTestFile(TestClass testClass) {
        Boolean isTestFile = false;
        try {
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(testClass.pathFile.toFile());
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
            testClass.numberLine = compilationUnit.getRange().get().end.line;
            detectJUnitVersion(compilationUnit.getImports(), testClass);
            List<NodeList<?>> nodeList = compilationUnit.getNodeLists();
            for (NodeList node : nodeList) {
                isTestFile = flowClass(node, testClass);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return isTestFile;
    }

    private static void detectJUnitVersion(NodeList<ImportDeclaration> nodeList, TestClass testClass) {
        for (ImportDeclaration node : nodeList) {
            if (node.getNameAsString().contains("org.junit.jupiter")) {
                testClass.junitVersion = TestClass.JunitVersion.JUnit5;
            } else if (node.getNameAsString().contains("org.junit")) {
                testClass.junitVersion = TestClass.JunitVersion.JUnit4;
            } else if (node.getNameAsString().contains("junit.framework")) {
                testClass.junitVersion = TestClass.JunitVersion.JUnit3;
            }
        }
    }

    private static Boolean flowClass(NodeList<?> nodeList, TestClass testClass) {
        boolean isTestClass = false;
        for (Object node : nodeList) {
            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classAtual = ((ClassOrInterfaceDeclaration) node);
                testClass.name = classAtual.getNameAsString();
                NodeList<?> nodeList_members = classAtual.getMembers();
                testClass.numberMethods = classAtual.getMembers().size();
                isTestClass = flowClass(nodeList_members, testClass);
            } else if (node instanceof MethodDeclaration) {
                isTestClass = flowClass(((MethodDeclaration) node).getAnnotations(), testClass);
            } else if (node instanceof MarkerAnnotationExpr) {
                return ((MarkerAnnotationExpr) node).getNameAsString().toLowerCase().equals("test");
            }
        }
        return isTestClass;
    }

    public static String getFileProduction(String directoryPath, String productionFileName) {
        final String[] retorno = {""};
        try {
            Path startDir = Paths.get(directoryPath);
            Files.walk(startDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        if (filePath.getFileName().toString().toLowerCase().equals(productionFileName.toLowerCase())) {
                            retorno[0] = filePath.toString();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno[0];
    }

    public static void getTestSmells(TestClass testClass) {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        TestFile testFile = new TestFile("Teste", testClass.pathFile.toString(), testClass.productionFile, testClass.numberLine, testClass.numberMethods);

        try {
            TestFile tempFile = testSmellDetector.detectSmells(testFile);
            for (AbstractSmell smell : tempFile.getTestSmells()) {
                smell.getSmellyElements();
                for (SmellyElement smellyElement : smell.getSmellyElements()) {
                    if (smellyElement.getHasSmell()) {
                        TestSmell testSmell = new TestSmell();
                        testSmell.name = smell.getSmellName();

                        testSmell.method = smellyElement.getElementName();
                        testSmell.begin = smellyElement.getData().get("begin");
                        testSmell.end = smellyElement.getData().get("end");
                        testSmell.lineNumber = smellyElement.getData().get("begin");

                        testClass.listTestSmell.add(testSmell);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
