package br.ufba.jnose.core;

import br.ufba.jnose.core.testfiledetector.entity.ClassEntity;
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
                                files.add(testClass);
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

    public static class TestClass{
        public Path pathFile;
        public String name;
        public Integer numberMethods;
        public Integer numberLine;

        @Override
        public String toString() {
            return "TestClass{" +
                    "pathFile=" + pathFile +
                    ", name='" + name + '\'' +
                    ", numberMethods=" + numberMethods +
                    ", numberLine=" + numberLine +
                    '}';
        }
    }

}
