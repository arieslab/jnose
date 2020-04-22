package br.ufba.jnose.core;

import br.ufba.jnose.core.testfiledetector.entity.ClassEntity;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.SmellyElement;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestFile;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JNoseUtils {

    private String filePath = "C:\\projetos\\commons-io\\src\\test\\java\\org\\apache\\commons\\io\\CopyUtilsTest.java";

    private static String directoryPath = "C:\\projetos\\commons-io";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World");
        Path startDir = Paths.get(directoryPath);

        List<TestClass> list = getFilesTest(directoryPath);

        list.parallelStream().forEach(f -> {
            System.out.println(f.toString());
        });
    }


    public static List<TestClass> getFilesTest(String directoryPath) throws IOException {
        List<TestClass> files = new ArrayList<>();
        Path startDir = Paths.get(directoryPath);
        Files.walk(startDir)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    if (filePath.getFileName().toString().lastIndexOf(".") != -1) {
                        String fileNameWithoutExtension = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().lastIndexOf(".")).toLowerCase();
                        if (filePath.toString().toLowerCase().endsWith(".java") && fileNameWithoutExtension.matches("^.*test\\d*$")){
                            TestClass testClass = new TestClass();
                            testClass.pathFile = filePath;
                            if (isTestFile(testClass)) {
                                System.out.println("TestClass Detect -> " + testClass.pathFile);
                                String productionFileName = "";
                                int index = testClass.name.toLowerCase().lastIndexOf("test");
                                if (index > 0) {
                                    productionFileName = testClass.name.substring(0, testClass.name.toLowerCase().lastIndexOf("test")) + ".java";
                                }
                                testClass.productionFile = getFileProduction(startDir.toString(),productionFileName);

                                if(!testClass.productionFile.isEmpty()){
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
            ClassEntity classEntity = new ClassEntity(testClass.pathFile);
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(classEntity.getFilePath());
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
            testClass.numberLine = compilationUnit.getRange().get().end.line;
            for (NodeList node : compilationUnit.getNodeLists()){
                isTestFile = navegarClass(node,testClass);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return isTestFile;
    }

    private static Boolean navegarClass(NodeList<?> nodeList,TestClass testClass) {
        boolean isTestClass = false;
        for (Object node : nodeList) {
            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classAtual = ((ClassOrInterfaceDeclaration) node);
                testClass.name = classAtual.getNameAsString();
                NodeList<?> nodeList_members = classAtual.getMembers();
                testClass.numberMethods = nodeList_members.size();
                isTestClass = navegarClass(nodeList_members,testClass);
            }else if(node instanceof MethodDeclaration) {
                isTestClass = navegarClass(((MethodDeclaration) node).getAnnotations(),testClass);
            }else if(node instanceof MarkerAnnotationExpr){
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return retorno[0];
    }

    public static void getTestSmells(TestClass testClass){
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        TestFile testFile = new TestFile("Teste",testClass.pathFile.toString(),testClass.productionFile,testClass.numberLine,testClass.numberMethods);
        //String app, String testFilePath, String productionFilePath, Integer loc, Integer qtdMethods
        try {
            TestFile tempFile = testSmellDetector.detectSmells(testFile);
            for (AbstractSmell smell : tempFile.getTestSmells()) {
                smell.getSmellyElements();
                for (SmellyElement smellyElement : smell.getSmellyElements()){
                    if(smellyElement.getHasSmell()){

//                        System.out.println(smellyElement);

                        TestSmell testSmell = new TestSmell();
                        testSmell.name = smell.getSmellName();
                        testSmell.method = smellyElement.getElementName();
//                        testSmell.lineNumber = smellyElement.xxxxx();

                        testClass.listTestSmell.add(testSmell);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TestClass{
        public Path pathFile;
        public String name;
        public Integer numberMethods;
        public Integer numberLine;
        public String productionFile;
        public List<TestSmell> listTestSmell = new ArrayList<>();

        @Override
        public String toString() {
            return "TestClass{" +
                    "pathFile=" + pathFile +
                    ", name='" + name + '\'' +
                    ", numberMethods=" + numberMethods +
                    ", numberLine=" + numberLine +
                    ", productionFile='" + productionFile + '\'' +
                    ", listTestSmell=" + listTestSmell +
                    '}';
        }
    }

    public static class TestSmell{
        public String name;
        public String method;
        public Long lineNumber;
        public String code;

        @Override
        public String toString() {
            return "TestSmell{" +
                    "name='" + name + '\'' +
                    ", method='" + method + '\'' +
                    ", lineNumber=" + lineNumber +
                    ", code='" + code + '\'' +
                    '}';
        }
    }

}
