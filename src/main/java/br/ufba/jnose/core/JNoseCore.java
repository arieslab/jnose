package br.ufba.jnose.core;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.dto.*;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.SmellyElement;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestFile;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.System.out;

public class JNoseCore {

    private final static Logger LOGGER = Logger.getLogger(JNoseCore.class.getName());

    public static List<String[]> testfilemapping(List<TestClass> listTestClass, Commit commit, String projectName) {
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

    public static String testfilemapping(List<TestClass> listTestClass, String pastaDataHora, String projectName) {
        System.out.println("Saving results. Total lines:" + listTestClass.size());

        List<List<String>> todasLinhas = new ArrayList<>();

        for (TestClass testClass : listTestClass) {
            List<String> columnValues = new ArrayList<>();
            columnValues.add(0, projectName);
            columnValues.add(1, testClass.pathFile.toString());
            columnValues.add(2, testClass.productionFile);
            columnValues.add(3, testClass.numberLine + "");
            columnValues.add(4, testClass.numberMethods + "");
            todasLinhas.add(columnValues);
        }
        System.out.println("Completed!");

        return CSVCore.criarTestmappingdetectorCSV(todasLinhas,pastaDataHora,projectName);
    }



    public static List<List<String>> convert(List<TestClass> listTestClass){

        List<List<String>> todasLinhas = new ArrayList<>();

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
        columnValues.add(9, "testSmellLineBegin");
        columnValues.add(10, "testSmellLineEnd");

        todasLinhas.add(columnValues);

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
                columnValues.add(9, testSmell.begin);
                columnValues.add(10, testSmell.end);
                todasLinhas.add(columnValues);
            }
        }

        return todasLinhas;
    }


    public static List<TestClass> getFilesTest(String directoryPath, StringBuffer logRetorno) throws IOException {
        String projectName = directoryPath.substring(directoryPath.lastIndexOf(File.separatorChar) + 1, directoryPath.length());

        logRetorno.insert(0,Util.dateNow() + projectName + " - <font style='color:red'>TestFileDetector</font> <br>");

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
                            testClass.pathFile = filePath.toString();
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

    public static TestClass.JunitVersion getJUnitVersion(String directoryPath) {
        String projectName = directoryPath.substring(directoryPath.lastIndexOf(File.separatorChar) + 1, directoryPath.length());

        final TestClass.JunitVersion[] jUnitVersion = new TestClass.JunitVersion[1];

        List<TestClass> files = new ArrayList<>();
        Path startDir = Paths.get(directoryPath);
        try {
            Files.walk(startDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        if (filePath.getFileName().toString().lastIndexOf(".") != -1) {
                            String fileNameWithoutExtension = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().lastIndexOf(".")).toLowerCase();
                            if (filePath.toString().toLowerCase().endsWith(".java") && fileNameWithoutExtension.matches("^.*test\\d*$")) {
                                TestClass testClass = new TestClass();
                                testClass.projectName = projectName;
                                testClass.pathFile = filePath.toString();
                                if (isTestFile(testClass)) {
                                    jUnitVersion[0] = testClass.junitVersion;

                                }
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jUnitVersion[0];
    }

    private static boolean isTestFile(TestClass testClass) {
        Boolean isTestFile = false;
        try {
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(new File(testClass.pathFile));
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
            testClass.numberLine = compilationUnit.getRange().get().end.line;
            detectJUnitVersion(compilationUnit.getImports(), testClass);
            List<NodeList<?>> nodeList = compilationUnit.getNodeLists();
            for (NodeList node : nodeList) {
                isTestFile = flowClass(node, testClass);
            }
        } catch (Exception e) {
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
                if(isTestClass)return true;
            } else if (node instanceof MethodDeclaration) {
                isTestClass = flowClass(((MethodDeclaration) node).getAnnotations(), testClass);
                if(isTestClass)return true;
            } else if (node instanceof AnnotationExpr) {
                return ((AnnotationExpr) node).getNameAsString().toLowerCase().contains("test");
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
//                        testSmell.lineNumber = smellyElement.getData().get("lineNumber");

                        testClass.listTestSmell.add(testSmell);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String processarTestSmellDetector(String pathCSVMapping, String pathProjeto, String folderTime, String pastaPathReport, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector</font> <br>");
        String csvTestSmells = "";
        try {
            csvTestSmells = br.ufba.jnose.core.testsmelldetector.Main.start(pathCSVMapping, nameProjeto, pastaPathReport + folderTime + File.separatorChar,folderTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvTestSmells;
    }


    public static List<List<String>> processarTestSmellDetector2(String pathCSVMapping, String pathProjeto, String folderTime, String pastaPathReport, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector</font> <br>");
        List<List<String>> todasLinhas = new ArrayList<>();
        try {
            todasLinhas = br.ufba.jnose.core.testsmelldetector.Main.start2(pathCSVMapping, nameProjeto, pastaPathReport + folderTime + File.separatorChar,folderTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todasLinhas;
    }

    public static String processarTestFileMapping(List<TestClass> listTestClass, String pathProjeto, String folderTime, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:green'>TestFileMapping</font> <br>");
        return JNoseCore.testfilemapping(listTestClass, folderTime, nameProjeto);
    }


    public static List<Projeto> listaProjetos(URI path, StringBuffer logRetornoInfo) {
        java.io.File[] directories = new File(path).listFiles(java.io.File::isDirectory);
        List<Projeto> lista = new ArrayList<Projeto>();

        if (directories != null) {
            for (java.io.File dir : directories) {
                String pathPom = dir.getAbsolutePath() + File.separatorChar + "pom.xml";

                if (new File(pathPom).exists()) {
                    String pathProjeto = dir.getAbsolutePath().trim();
                    String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
                    lista.add(new Projeto(nameProjeto, pathProjeto));
                } else {
                    String msg = "It is not a project MAVEN: " + dir.getAbsolutePath();
                    out.println(msg);
                }
            }
        }

        return lista;
    }


    public static List<TestClass> processarProjeto(Projeto projeto, StringBuffer logRetorno) throws IOException {
        projeto.setProcentagem(25);
        List<TestClass> listaTestClass = JNoseCore.getFilesTest(projeto.getPath(), logRetorno);
        projeto.setProcentagem(100);
        projeto.setProcessado(true);
        projeto.setProcessado2(true);
        return listaTestClass;
    }


    public static List<List<String>> processarProjeto2(Projeto projeto, float valorProcProject, String folderTime, TotalProcessado totalProcessado, String pastaPathReport, StringBuffer logRetorno) throws IOException {
        logRetorno.insert(0,Util.dateNow() + projeto.getName() + " - started <br>");
        Float valorSoma = valorProcProject / 4;

        totalProcessado.setValor(5);
        projeto.setProcentagem(totalProcessado.getValor());

        if (WicketApplication.COBERTURA_ON) {
            CoverageCore.processarCobertura(projeto, folderTime, pastaPathReport, logRetorno);
        }

        projeto.setProcessado2(true);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(25);

        List<TestClass> listaTestClass = JNoseCore.getFilesTest(projeto.getPath(),logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(50);

        String csvMapping = JNoseCore.processarTestFileMapping(listaTestClass, projeto.getPath(), folderTime, logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(75);

        List<List<String>> todasLinhas  =  JNoseCore.processarTestSmellDetector2(csvMapping, projeto.getPath(), folderTime, pastaPathReport, logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(100);

        projeto.setProcessado(true);
        return todasLinhas;
    }


    public static List<List<String>> processarProjetos2(List<Projeto> lista, String folderTime, TotalProcessado totalProcessado, String pastaPathReport, StringBuffer logRetorno) {

        boolean success = (new File(pastaPathReport + folderTime + File.separatorChar)).mkdirs();
        if (!success) System.out.println("Created Folder...");

        totalProcessado.setValor(0);

        Integer totalLista = lista.size();
        Integer valorSoma;
        if (totalLista > 0) {
            valorSoma = 100 / totalLista;
        } else {
            valorSoma = 0;
        }

        List<List<String>> listaTodos = new ArrayList<>();

        for (Projeto projeto : lista) {
            new Thread() { // IMPORTANTE: AQUI SE CRIA AS THREADS
                @Override
                public void run() {
                    try {
                        List<List<String>> todasLinhas = JNoseCore.processarProjeto2(projeto, valorSoma, folderTime, totalProcessado, pastaPathReport, logRetorno);
                        projeto.setResultado(todasLinhas);
                        listaTodos.addAll(todasLinhas);
                    } catch (Exception e) {
                        e.printStackTrace();
                        projeto.bugs = projeto.bugs + "\n" + e.getMessage();
                    }
                }
            }.start();
        }

        return listaTodos;

    }

    public static void processarProjetos(List<Projeto> lista, String folderTime,String pastaPathReport, TotalProcessado totalProcessado, StringBuffer logRetorno) {

        boolean success = (new File(pastaPathReport + folderTime + File.separatorChar)).mkdirs();
        if (!success) System.out.println("Created Folder...");

        totalProcessado.setValor(0);

        Integer totalLista = lista.size();
        Integer valorSoma;
        if (totalLista > 0) {
            valorSoma = 100 / totalLista;
        } else {
            valorSoma = 0;
        }

        List<TestClass> listaTestClass = new ArrayList<>();

        for (Projeto projeto : lista) {
            try {
                List<TestClass> todasLinhas = JNoseCore.processarProjeto(projeto, logRetorno);
                projeto.setResultadoByTestSmells(todasLinhas);
                projeto.setResultado(JNoseCore.convert(todasLinhas));
                listaTestClass.addAll(todasLinhas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static List<String[]> processarTestSmells(String pathProjeto, Commit commit, Boolean cabecalho, StringBuffer logRetorno) {
        List<String[]> listTestSmells = null;
        try {
            List<TestClass> listTestFile = JNoseCore.getFilesTest(pathProjeto,logRetorno);

            if(pathProjeto.lastIndexOf(File.separator) + 1 == pathProjeto.length()){
                pathProjeto = pathProjeto.substring(0,pathProjeto.lastIndexOf(File.separator)-1);
            }

            String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separator) + 1, pathProjeto.length());
            List<String[]> listaResultado = JNoseCore.testfilemapping(listTestFile, commit, nameProjeto);
            listTestSmells = br.ufba.jnose.core.testsmelldetector.Main.start(listaResultado, cabecalho);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTestSmells;
    }


    public static void processarEvolution(Projeto projeto, StringBuffer logRetorno, Map<Integer, List<List<String>>> mapa) {

        GitCore.checkout("master", projeto.getPath());

        List<Commit> lista;

        if (projeto.getOptionSelected().contains("/")) {
            lista = projeto.getListaCommits();
        } else {
            lista = projeto.getListaTags();
        }

        Collections.sort(lista, new Comparator<Commit>() {
            public int compare(Commit o1, Commit o2) {
                if (o1.date == null || o2.date == null) return 0;
                return o2.date.compareTo(o1.date);
            }
        });

        List<List<String>> todasLinhas1 = new ArrayList<>();
        List<List<String>> todasLinhas2 = new ArrayList<>();

        boolean vizualizarCabecalho = true;

        //Para cada commit executa uma busca
        for (Commit commit : lista) {

            logRetorno.insert(0,"Analyze commit: " + commit.id + "<br>");

            GitCore.checkout(commit.id, projeto.getPath());

            int total = 0;
            //criando a lista de testsmells
            List<String[]> listaTestSmells = JNoseCore.processarTestSmells(projeto.getPath(), commit, vizualizarCabecalho, logRetorno);
            for (String[] linhaArray : listaTestSmells) {
                List<String> list = Arrays.asList(linhaArray);
                for (int i = 10; i <= (list.size() - 1); i++) {
                    boolean isNumeric = list.get(i).chars().allMatch(Character::isDigit);
                    if (isNumeric) {
                        total += Integer.parseInt(list.get(i));
                    }
                }
                todasLinhas1.add(list);
            }

            List<String> lista2 = new ArrayList<>();
            lista2.add(commit.id);
            lista2.add(commit.tag);
            lista2.add(commit.date + "");
            lista2.add(total + "");
            todasLinhas2.add(lista2);
            vizualizarCabecalho = false;
        }

        mapa.put(1, todasLinhas1);
        mapa.put(2, todasLinhas2);

        GitCore.checkout("master", projeto.getPath());
    }


}

