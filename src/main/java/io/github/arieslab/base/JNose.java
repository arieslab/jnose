package io.github.arieslab.base;

import io.github.arieslab.WicketApplication;
import io.github.arieslab.core.Config;
import io.github.arieslab.core.JNoseCore;
import io.github.arieslab.core.testsmelldetector.testsmell.TestSmellDetector;
import io.github.arieslab.dto.TestSmell;
import io.github.arieslab.dtolocal.Commit;
import io.github.arieslab.dtolocal.ProjetoDTO;
import io.github.arieslab.dto.TestClass;
import io.github.arieslab.dtolocal.TotalProcessado;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JNose {

    private static final Logger LOGGER = Logger.getLogger(JNose.class.getName());

    private static JNoseCore jNoseCore;

    /**
     * Returns the singleton JNoseCore instance, creating it with the current test smell detector configuration if necessary.
     *
     * @return the shared JNoseCore instance
     */
    public static JNoseCore getInstanceJNoseCore(){

        Config conf = new Config() {
            public boolean assertionRoulette() {return TestSmellDetectorConfig.assertionRoulette;}
            public boolean conditionalTestLogic() {
                return TestSmellDetectorConfig.conditionalTestLogic;
            }
            public boolean constructorInitialization() {
                return TestSmellDetectorConfig.constructorInitialization;
            }
            public boolean defaultTest() {
                return TestSmellDetectorConfig.defaultTest;
            }
            public boolean dependentTest() {
                return TestSmellDetectorConfig.dependentTest;
            }
            public boolean duplicateAssert() {
                return TestSmellDetectorConfig.duplicateAssert;
            }
            public boolean eagerTest() {
                return TestSmellDetectorConfig.eagerTest;
            }
            public boolean emptyTest() {
                return TestSmellDetectorConfig.emptyTest;
            }
            public boolean exceptionCatchingThrowing() {
                return TestSmellDetectorConfig.exceptionCatchingThrowing;
            }
            public boolean generalFixture() {return TestSmellDetectorConfig.generalFixture;}
            public boolean mysteryGuest() {
                return TestSmellDetectorConfig.mysteryGuest;
            }
            public boolean printStatement() {
                return TestSmellDetectorConfig.printStatement;
            }
            public boolean redundantAssertion() {
                return TestSmellDetectorConfig.redundantAssertion;
            }
            public boolean sensitiveEquality() {
                return TestSmellDetectorConfig.sensitiveEquality;
            }
            public boolean verboseTest() {
                return TestSmellDetectorConfig.verboseTest;
            }
            public boolean sleepyTest() {
                return TestSmellDetectorConfig.sleepyTest;
            }
            public boolean lazyTest() {
                return TestSmellDetectorConfig.lazyTest;
            }
            public boolean unknownTest() {
                return TestSmellDetectorConfig.unknownTest;
            }
            public boolean ignoredTest() {
                return TestSmellDetectorConfig.ignoredTest;
            }
            public boolean resourceOptimism() {
                return TestSmellDetectorConfig.resourceOptimism;
            }
            public boolean magicNumberTest() {return TestSmellDetectorConfig.magicNumberTest;}

            @Override
            public int maxStatements() {
                return 30;
            }
        };

        if(jNoseCore == null) {
            jNoseCore = new JNoseCore(conf);
        }

        return jNoseCore;
    }

    static {
        jNoseCore = getInstanceJNoseCore();
    }

    public JNose(){
        jNoseCore = getInstanceJNoseCore();
    }

    /**
     * Converts a list of TestClass results into a tabular format (list of rows) suitable for CSV export.
     *
     * @param listTestClass the list of analyzed test classes
     * @return a list of rows, where the first row contains column headers
     */
    public static List<List<String>> convert(List<TestClass> listTestClass){

        List<List<String>> todasLinhas = new ArrayList<>();

        List<String> columnValues;
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
        columnValues.add(11, "methodNameHash");
        columnValues.add(12, "methodNameFullHash");
        columnValues.add(13, "methodCode");
        columnValues.add(14, "methodCodeHash");
        columnValues.add(15, "FullHash");

        todasLinhas.add(columnValues);

        for (TestClass testClass : listTestClass) {
            for (TestSmell testSmell : testClass.getListTestSmell()) {
                columnValues = new ArrayList<>();
                columnValues.add(0, testClass.getProjectName());
                columnValues.add(1, testClass.getName());
                columnValues.add(2, testClass.getPathFile());
                columnValues.add(3, testClass.getProductionFile());
                columnValues.add(4, testClass.getJunitVersion().name());
                columnValues.add(5, testClass.getNumberLine().toString());
                columnValues.add(6, testClass.getNumberMethods().toString());
                columnValues.add(7, testSmell.getName());
                columnValues.add(8, testSmell.getMethod());
                columnValues.add(9, testSmell.getRange());
                columnValues.add(10, testSmell.getRange());
                columnValues.add(11, testSmell.getMethodNameHash());
                columnValues.add(12, testSmell.getMethodNameFullURIHash());

                if(testSmell.getRange().contains("-")){
                    int start = Integer.parseInt(testSmell.getRange().replaceAll(" ","").split("-")[0]);
                    int end = Integer.parseInt(testSmell.getRange().replaceAll(" ","").split("-")[1]);
                    String code_ = getSource(testClass.getPathFile(),start,end);
                    code_ = code_.replace("/\\r?\\n|\\r/", "");
                    columnValues.add(13, code_);
                    columnValues.add(14, hash(code_));
                    columnValues.add(15, hash(code_+testSmell.getMethodNameFullURIHash()));
                }else if(testSmell.getRange().contains(",")) {
                    String[] lista = testSmell.getRange().split(",");
                    String code_ = getSource(testClass.getPathFile(),lista);
                    code_ = code_.replace("/\\r?\\n|\\r/", "");
                    columnValues.add(13, code_);
                    columnValues.add(14, hash(code_));
                    columnValues.add(15, hash(code_+testSmell.getMethodNameFullURIHash()));
                }

                todasLinhas.add(columnValues);
            }
        }

        return todasLinhas;
    }

    /**
     * Computes an MD5 hash of the given string.
     *
     * @param string the input string
     * @return the MD5 hash as a 32-character hexadecimal string
     */
    public static String hash(String string) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(string));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to hash", e);
            return "";
        }
    }


    /**
     * Reads source code lines within a line range from a file.
     *
     * @param pathFile the path to the source file
     * @param start the start line (1-indexed)
     * @param end the end line (1-indexed)
     * @return concatenated source code with whitespace and semicolons normalized
     */
    private static String getSource(String pathFile, int start, int end) {
        StringBuilder stringBuilder = new StringBuilder();
        Path path = Paths.get(pathFile);

        if(start >= 1) start--;
        if(end >= 1) end--;

        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = start; i <= end; i++) {
                String textoLinha = lines.get(i).trim().replaceAll(" ","").replaceAll(";","|");
                stringBuilder.append(textoLinha);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to read source: " + pathFile, ex);
        }
        return stringBuilder.toString();
    }

    /**
     * Reads source code at specific line numbers from a file.
     *
     * @param pathFile the path to the source file
     * @param lista an array of line numbers (1-indexed) as strings
     * @return concatenated source code with whitespace and semicolons normalized
     */
    private static String getSource(String pathFile, String[] lista) {
        StringBuilder stringBuilder = new StringBuilder();
        Path path = Paths.get(pathFile);
        try{
            List<String> lines = Files.readAllLines(path);
            for (String i : lista) {
                int number = Integer.parseInt(i.trim());
                if(number >= 1) number--;
                String textoLinha = lines.get(number).replaceAll(" ","").replaceAll(";","|");
                stringBuilder.append(textoLinha);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to read source: " + pathFile, ex);
        }
        return stringBuilder.toString();
    }


    /**
     * Detects the JUnit version used in the given project directory.
     *
     * @param directoryPath the project directory path
     * @return the detected JUnit version
     */
    public static TestClass.JunitVersion getJUnitVersion(String directoryPath) {
        return getInstanceJNoseCore().getJUnitVersion(directoryPath);
    }


    /**
     * Runs the test smell detector on a project and returns results in tabular format.
     *
     * @param pathProjeto the project directory path
     * @param logRetorno a buffer for appending log messages
     * @return a list of rows where the first row contains column headers
     */
    public static List<List<String>> processarTestSmellDetector2(String pathProjeto, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1);
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector novo</font> <br>");
        List<List<String>> todasLinhas = new ArrayList<>();

        try {
            List<TestClass> listFileTests = getInstanceJNoseCore().getFilesTest(pathProjeto);
            List<String> linhacolunas = new ArrayList<>();
            linhacolunas.add("App");
            linhacolunas.add("TestFileName");
            linhacolunas.add("PathFile");
            linhacolunas.add("ProductionFileName");
            linhacolunas.add("LOC");
            linhacolunas.add("numberMethods");
            listFileTests.get(0).getLineSumTestSmells().keySet().stream().forEach(v -> linhacolunas.add(v));
            todasLinhas.add(linhacolunas);

            for(TestClass testClass : listFileTests){
                List<String> linha = new ArrayList<>();
                linha.add(testClass.getProjectName());
                linha.add(testClass.getName());
                linha.add(testClass.getPathFile());
                linha.add(testClass.getProductionFile());
                linha.add(testClass.getNumberLine().toString());
                linha.add(testClass.getNumberMethods().toString());
                testClass.getLineSumTestSmells().values().stream().forEach(v -> linha.add(v.toString()));
                todasLinhas.add(linha);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to process test smell detector for: " + pathProjeto, e);
        }

        return todasLinhas;
    }


    /**
     * Processes a single project, detecting test smells and returning the analyzed test classes.
     *
     * @param projeto the project DTO
     * @return list of analyzed test classes
     */
    public static List<TestClass> processarProjeto(ProjetoDTO projeto) throws Exception {
        projeto.setProcentagem(25);
        List<TestClass> listaTestClass = getInstanceJNoseCore().getFilesTest(projeto.getPath());
        projeto.setProcentagem(100);
        projeto.setProcessado(true);
        projeto.setProcessado2(true);
        return listaTestClass;
    }


    /**
     * Processes a single project with progress tracking, optional coverage analysis, and log output.
     *
     * @param projeto the project DTO
     * @param valorProcProject the progress value allocated to this project
     * @param folderTime the timestamp folder for report output
     * @param totalProcessado shared progress tracker
     * @param pastaPathReport the base report directory path
     * @param logRetorno a buffer for appending log messages
     * @return results in tabular format
     */
    public static List<List<String>> processarProjeto2(ProjetoDTO projeto, float valorProcProject, String folderTime, TotalProcessado totalProcessado, String pastaPathReport, StringBuffer logRetorno) throws IOException {
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

        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(50);

        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(75);

        List<List<String>> todasLinhas  =  JNose.processarTestSmellDetector2(projeto.getPath(), logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(100);

        projeto.setProcessado(true);
        return todasLinhas;
    }


    /**
     * Processes a list of projects concurrently, each in its own thread, collecting all results.
     *
     * @param lista list of project DTOs to process
     * @param folderTime the timestamp folder for report output
     * @param totalProcessado shared progress tracker
     * @param pastaPathReport the base report directory path
     * @param logRetorno a buffer for appending log messages
     * @return aggregated results from all projects in tabular format
     */
    public static List<List<String>> processarProjetos2(List<ProjetoDTO> lista, String folderTime, TotalProcessado totalProcessado, String pastaPathReport, StringBuffer logRetorno) {

        boolean success = (new File(pastaPathReport + folderTime + File.separatorChar)).mkdirs();
        if (!success) LOGGER.log(Level.FINE, "Created Folder...");

        totalProcessado.setValor(0);

        Integer totalLista = lista.size();
        Integer valorSoma;
        if (totalLista > 0) {
            valorSoma = 100 / totalLista;
        } else {
            valorSoma = 0;
        }

        List<List<String>> listaTodos = new ArrayList<>();

        for (ProjetoDTO projeto : lista) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        List<List<String>> todasLinhas = JNose.processarProjeto2(projeto, valorSoma, folderTime, totalProcessado, pastaPathReport, logRetorno);
                        projeto.setResultado(todasLinhas);
                        listaTodos.addAll(todasLinhas);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed processing project: " + projeto.getName(), e);
                        projeto.bugs = projeto.bugs + "\n" + e.getMessage();
                    }
                }
            }.start();
        }

        return listaTodos;

    }

    /**
     * Processes a list of projects sequentially, detecting test smells and collecting results.
     *
     * @param lista list of project DTOs to process
     * @param folderTime the timestamp folder for report output
     * @param pastaPathReport the base report directory path
     * @param totalProcessado shared progress tracker
     */
    public static void processarProjetos(List<ProjetoDTO> lista, String folderTime,String pastaPathReport, TotalProcessado totalProcessado) {

        boolean success = (new File(pastaPathReport + folderTime + File.separatorChar)).mkdirs();
        if (!success) LOGGER.log(Level.FINE, "Created Folder...");

        totalProcessado.setValor(0);

        Integer totalLista = lista.size();
        Integer valorSoma;
        if (totalLista > 0) {
            valorSoma = 100 / totalLista;
        } else {
            valorSoma = 0;
        }

        List<TestClass> listaTestClass = new ArrayList<>();

        for (ProjetoDTO projeto : lista) {
            try {
                List<TestClass> todasLinhas = JNose.processarProjeto(projeto);
                projeto.setResultadoByTestSmells(todasLinhas);
                projeto.setResultado(JNose.convert(todasLinhas));
                listaTestClass.addAll(todasLinhas);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed processing project: " + projeto.getName(), e);
            }
        }
    }


    /**
     * Processes the evolution of test smells across multiple commits/tags of a project.
     *
     * @param projeto the project DTO with selected commits or tags
     * @param logRetorno a buffer for appending log messages
     * @param mapa a map to store result tables: 1=full data, 2=summary, 3=smell details, 4=deduplicated, 5=author counts
     */
    public static void processarEvolution(ProjetoDTO projeto, StringBuffer logRetorno, Map<Integer, List<List<String>>> mapa) {
        final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        GitCore.checkout("master", projeto.getPath());

        List<Commit> lista = new ArrayList<>();

        if ("commit".equalsIgnoreCase(projeto.getOptionSelected())) {
            lista = projeto.getListaCommits();
        } else if ("tag".equalsIgnoreCase(projeto.getOptionSelected())) {
            lista = projeto.getListaTags();
        }

        if (lista.isEmpty()) {
            logRetorno.insert(0, "Nenhum commit ou tag selecionado<br>");
            return;
        }

        lista.sort(Comparator.comparing(Commit::date, Comparator.nullsLast(Comparator.naturalOrder())));

        List<List<String>> todasLinhas1 = new ArrayList<>();
        List<List<String>> todasLinhas2 = new ArrayList<>();
        List<List<String>> todasLinhas3 = new ArrayList<>();
        List<List<String>> todasLinhas4 = new ArrayList<>();
        List<List<String>> todasLinhas5 = new ArrayList<>();

        int cont = 1;
        boolean primeiraLinha = true;
        Set<String> jaProcessado = new HashSet<>();

        JNoseCore core = getInstanceJNoseCore();
        int numberThread = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService threadpool = Executors.newFixedThreadPool(numberThread);

        try {
            for (Commit commit : lista) {
                logRetorno.insert(0, cont++ + " - Analyze commit: " + commit.id() + "<br>");

                try {
                    GitCore.checkout(commit.id(), projeto.getPath());

                    List<TestClass> listTestClass = core.getFilesTest(projeto.getPath(), threadpool);

                if (!listTestClass.isEmpty() && primeiraLinha) {
                    primeiraLinha = false;
                    List<String> listaColumName = new ArrayList<>();
                    listaColumName.add("commit.id()");
                    listaColumName.add("commit.authorName()");
                    listaColumName.add("commit.date()");
                    listaColumName.add("commit.msg()");
                    listaColumName.add("commit.tag()");
                    listaColumName.add("project");
                    listaColumName.add("TestClass");
                    listaColumName.add("PathFile");
                    listaColumName.add("ProductionFile");
                    listaColumName.add("NumberLine");
                    listaColumName.add("NumberMethods");
                    listaColumName.addAll(TestSmellDetector.getAllTestSmellNames());
                    todasLinhas1.add(listaColumName);
                }

                int totalTestSmells = 0;

                for (TestClass testClass : listTestClass) {
                    List<String> lista3 = new ArrayList<>();
                    lista3.add(commit.id());
                    lista3.add(commit.authorName());
                    lista3.add(commit.date() != null ? DATE_FMT.format(commit.date()) : "");
                    lista3.add(commit.msg());
                    lista3.add(commit.tag() != null ? commit.tag() : "");
                    lista3.add(projeto.getName());
                    lista3.add(testClass.getName());
                    lista3.add(testClass.getPathFile());
                    lista3.add(testClass.getProductionFile());
                    lista3.add(testClass.getNumberLine().toString());
                    lista3.add(testClass.getNumberMethods().toString());
                    testClass.getLineSumTestSmells().values().stream().forEach(v -> lista3.add(v.toString()));
                    todasLinhas1.add(lista3);

                    for (TestSmell ts : testClass.getListTestSmell()) {
                        String sha256 = Util.getSHA5Code(testClass, ts).trim();

                        if (jaProcessado.add(sha256)) {
                            totalTestSmells++;

                            List<String> lista4 = new ArrayList<>();
                            lista4.add(commit.id());
                            lista4.add(commit.authorName());
                            lista4.add(commit.date() != null ? DATE_FMT.format(commit.date()) : "");
                            lista4.add(commit.msg());
                            lista4.add(commit.tag() != null ? commit.tag() : "");
                            lista4.add(projeto.getName());
                            lista4.add(testClass.getName());
                            lista4.add(testClass.getPathFile());
                            lista4.add(testClass.getProductionFile());
                            lista4.add(testClass.getNumberLine().toString());
                            lista4.add(testClass.getNumberMethods().toString());
                            lista4.add(ts.getName());
                            lista4.add(ts.getMethod());
                            lista4.add(ts.getRange());
                            lista4.add(sha256);
                            todasLinhas3.add(lista4);
                        }
                    }
                }

                List<String> lista2 = new ArrayList<>();
                lista2.add(commit.id());
                lista2.add(commit.tag() != null ? commit.tag() : "");
                lista2.add(commit.date() != null ? DATE_FMT.format(commit.date()) : "");
                lista2.add(String.valueOf(totalTestSmells));
                todasLinhas2.add(lista2);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed processing evolution for commit: " + commit.id(), e);
            }
        }

        mapa.put(1, todasLinhas1);
        mapa.put(2, todasLinhas2);
        mapa.put(3, todasLinhas3);

        Set<String> setSHA = new HashSet<>();
        Map<String, Integer> mapName = new HashMap<>();

        for (List<String> linha : todasLinhas3) {
            if (setSHA.add(linha.get(14))) {
                todasLinhas4.add(linha);
            }
            mapName.merge(linha.get(1), 1, Integer::sum);
        }

        mapa.put(4, todasLinhas4);

        mapName.forEach((s, i) -> {
            List<String> lista3 = new ArrayList<>();
            lista3.add(s);
            lista3.add(i.toString());
            todasLinhas5.add(lista3);
        });

        mapa.put(5, todasLinhas5);

        GitCore.checkout("master", projeto.getPath());
        } finally {
            threadpool.shutdown();
        }
    }


}
