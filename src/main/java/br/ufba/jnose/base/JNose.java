package br.ufba.jnose.base;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.base.testsmelldetector.Main;
import br.ufba.jnose.core.Config;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dtolocal.Commit;
import br.ufba.jnose.dtolocal.ProjetoDTO;
import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.dtolocal.TotalProcessado;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class JNose {

    private static JNoseCore jNoseCore;

    public static JNoseCore getInstance(){

        Config conf = new Config() {
            public Boolean assertionRoulette() {
                return true;
            }
            public Boolean conditionalTestLogic() {
                return true;
            }
            public Boolean constructorInitialization() {
                return true;
            }
            public Boolean defaultTest() {
                return true;
            }
            public Boolean dependentTest() {
                return true;
            }
            public Boolean duplicateAssert() {
                return true;
            }
            public Boolean eagerTest() {
                return true;
            }
            public Boolean emptyTest() {
                return true;
            }
            public Boolean exceptionCatchingThrowing() {
                return true;
            }
            public Boolean generalFixture() {
                return true;
            }
            public Boolean mysteryGuest() {
                return true;
            }
            public Boolean printStatement() {
                return true;
            }
            public Boolean redundantAssertion() {
                return true;
            }
            public Boolean sensitiveEquality() {
                return true;
            }
            public Boolean verboseTest() {
                return true;
            }
            public Boolean sleepyTest() {
                return true;
            }
            public Boolean lazyTest() {
                return true;
            }
            public Boolean unknownTest() {
                return true;
            }
            public Boolean ignoredTest() {
                return true;
            }
            public Boolean resourceOptimism() {
                return true;
            }
            public Boolean magicNumberTest() {
                return true;
            }
        };

        if(jNoseCore == null) {
            jNoseCore = new JNoseCore(conf);
        }

        return jNoseCore;
    }

    private final static Logger LOGGER = Logger.getLogger(JNose.class.getName());

    public static List<String[]> testfilemapping(List<TestClass> listTestClass, Commit commit, String projectName) {
        System.out.println("Saving results. Total lines:" + listTestClass.size());

        List<String[]> listRetorno = new ArrayList<>();

        for (br.ufba.jnose.dto.TestClass testClass : listTestClass) {
            String[] linha = {
                    commit.id,
                    commit.name,
                    commit.date.toString(),
                    commit.msg,
                    commit.tag,
                    projectName,
                    testClass.getPathFile(),
                    testClass.getProductionFile(),
                    testClass.getNumberLine().toString(),
                    testClass.getNumberMethods().toString()
            };
            listRetorno.add(linha);
        }
        return listRetorno;
    }

    public static String testfilemapping(List<TestClass> listTestClass, String pastaDataHora, String projectName) {
        System.out.println("Saving results. Total lines:" + listTestClass.size());

        List<List<String>> todasLinhas = new ArrayList<>();

        for (br.ufba.jnose.dto.TestClass testClass : listTestClass) {
            List<String> columnValues = new ArrayList<>();
            columnValues.add(0, projectName);
            columnValues.add(1, testClass.getPathFile());
            columnValues.add(2, testClass.getProductionFile());
            columnValues.add(3, testClass.getNumberLine() + "");
            columnValues.add(4, testClass.getNumberMethods() + "");
            todasLinhas.add(columnValues);
        }
        System.out.println("Completed!");

        return CSVCore.criarTestmappingdetectorCSV(todasLinhas,pastaDataHora,projectName);
    }



    public static List<List<String>> convert(List<br.ufba.jnose.dto.TestClass> listTestClass){

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

        for (br.ufba.jnose.dto.TestClass testClass : listTestClass) {
            for (br.ufba.jnose.dto.TestSmell testSmell : testClass.getListTestSmell()) {
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
                todasLinhas.add(columnValues);
            }
        }

        return todasLinhas;
    }

    public static List<TestClass> getFilesTest(String directoryPath, StringBuffer logRetorno) throws IOException {
        return getInstance().getFilesTest(directoryPath);
    }

    public static TestClass.JunitVersion getJUnitVersion(String directoryPath) {
        return getInstance().getJUnitVersion(directoryPath);
    }

    public static void getTestSmells(br.ufba.jnose.dto.TestClass testClass) {
        getInstance().getTestSmells(testClass);
    }

    public static String processarTestSmellDetector(String pathCSVMapping, String pathProjeto, String folderTime, String pastaPathReport, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector</font> <br>");
        String csvTestSmells = "";
        try {
            csvTestSmells = Main.start(pathCSVMapping, nameProjeto, pastaPathReport + folderTime + File.separatorChar,folderTime);
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
            todasLinhas = Main.start2(pathCSVMapping, nameProjeto, pastaPathReport + folderTime + File.separatorChar,folderTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todasLinhas;
    }

    public static String processarTestFileMapping(List<br.ufba.jnose.dto.TestClass> listTestClass, String pathProjeto, String folderTime, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:green'>TestFileMapping</font> <br>");
        return JNose.testfilemapping(listTestClass, folderTime, nameProjeto);
    }


    public static List<br.ufba.jnose.dto.TestClass> processarProjeto(ProjetoDTO projeto, StringBuffer logRetorno) throws IOException {
        projeto.setProcentagem(25);
        List<br.ufba.jnose.dto.TestClass> listaTestClass = JNose.getFilesTest(projeto.getPath(), logRetorno);
        projeto.setProcentagem(100);
        projeto.setProcessado(true);
        projeto.setProcessado2(true);
        return listaTestClass;
    }


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

        List<br.ufba.jnose.dto.TestClass> listaTestClass = JNose.getFilesTest(projeto.getPath(),logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(50);

        String csvMapping = JNose.processarTestFileMapping(listaTestClass, projeto.getPath(), folderTime, logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(75);

        List<List<String>> todasLinhas  =  JNose.processarTestSmellDetector2(csvMapping, projeto.getPath(), folderTime, pastaPathReport, logRetorno);
        totalProcessado.setValor(totalProcessado.getValor() + valorSoma.intValue());
        projeto.setProcentagem(100);

        projeto.setProcessado(true);
        return todasLinhas;
    }


    public static List<List<String>> processarProjetos2(List<ProjetoDTO> lista, String folderTime, TotalProcessado totalProcessado, String pastaPathReport, StringBuffer logRetorno) {

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

        for (ProjetoDTO projeto : lista) {
            new Thread() { // IMPORTANTE: AQUI SE CRIA AS THREADS
                @Override
                public void run() {
                    try {
                        List<List<String>> todasLinhas = JNose.processarProjeto2(projeto, valorSoma, folderTime, totalProcessado, pastaPathReport, logRetorno);
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

    public static void processarProjetos(List<ProjetoDTO> lista, String folderTime,String pastaPathReport, TotalProcessado totalProcessado, StringBuffer logRetorno) {

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

        for (ProjetoDTO projeto : lista) {
            try {
                List<br.ufba.jnose.dto.TestClass> todasLinhas = JNose.processarProjeto(projeto, logRetorno);
                projeto.setResultadoByTestSmells(todasLinhas);
                projeto.setResultado(JNose.convert(todasLinhas));
                listaTestClass.addAll(todasLinhas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static List<String[]> processarTestSmells(String pathProjeto, Commit commit, Boolean cabecalho, StringBuffer logRetorno) {
        List<String[]> listTestSmells = null;
        try {
            List<TestClass> listTestFile = JNose.getFilesTest(pathProjeto,logRetorno);

            if(pathProjeto.lastIndexOf(File.separator) + 1 == pathProjeto.length()){
                pathProjeto = pathProjeto.substring(0,pathProjeto.lastIndexOf(File.separator)-1);
            }

            String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separator) + 1, pathProjeto.length());
            List<String[]> listaResultado = JNose.testfilemapping(listTestFile, commit, nameProjeto);
            listTestSmells = Main.start(listaResultado, cabecalho);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTestSmells;
    }


    public static void processarEvolution(ProjetoDTO projeto, StringBuffer logRetorno, Map<Integer, List<List<String>>> mapa) {

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
            List<String[]> listaTestSmells = JNose.processarTestSmells(projeto.getPath(), commit, vizualizarCabecalho, logRetorno);
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

