package br.ufba.jnose.base;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.core.Config;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.TestSmell;
import br.ufba.jnose.dtolocal.Commit;
import br.ufba.jnose.dtolocal.ProjetoDTO;
import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.dtolocal.TotalProcessado;

import java.io.*;
import java.util.*;

public class JNose {

    private static JNoseCore jNoseCore;

    public static JNoseCore getInstanceJNoseCore(){

        Config conf = new Config() {
            public Boolean assertionRoulette() {return TestSmellDetectorConfig.assertionRoulette;}
            public Boolean conditionalTestLogic() {
                return TestSmellDetectorConfig.conditionalTestLogic;
            }
            public Boolean constructorInitialization() {
                return TestSmellDetectorConfig.constructorInitialization;
            }
            public Boolean defaultTest() {
                return TestSmellDetectorConfig.defaultTest;
            }
            public Boolean dependentTest() {
                return TestSmellDetectorConfig.dependentTest;
            }
            public Boolean duplicateAssert() {
                return TestSmellDetectorConfig.duplicateAssert;
            }
            public Boolean eagerTest() {
                return TestSmellDetectorConfig.eagerTest;
            }
            public Boolean emptyTest() {
                return TestSmellDetectorConfig.emptyTest;
            }
            public Boolean exceptionCatchingThrowing() {
                return TestSmellDetectorConfig.exceptionCatchingThrowing;
            }
            public Boolean generalFixture() {return TestSmellDetectorConfig.generalFixture;}
            public Boolean mysteryGuest() {
                return TestSmellDetectorConfig.mysteryGuest;
            }
            public Boolean printStatement() {
                return TestSmellDetectorConfig.printStatement;
            }
            public Boolean redundantAssertion() {
                return TestSmellDetectorConfig.redundantAssertion;
            }
            public Boolean sensitiveEquality() {
                return TestSmellDetectorConfig.sensitiveEquality;
            }
            public Boolean verboseTest() {
                return TestSmellDetectorConfig.verboseTest;
            }
            public Boolean sleepyTest() {
                return TestSmellDetectorConfig.sleepyTest;
            }
            public Boolean lazyTest() {
                return TestSmellDetectorConfig.lazyTest;
            }
            public Boolean unknownTest() {
                return TestSmellDetectorConfig.unknownTest;
            }
            public Boolean ignoredTest() {
                return TestSmellDetectorConfig.ignoredTest;
            }
            public Boolean resourceOptimism() {
                return TestSmellDetectorConfig.resourceOptimism;
            }
            public Boolean magicNumberTest() {return TestSmellDetectorConfig.magicNumberTest;}

            @Override
            public Integer maxStatements() {
                return 30;
            }
        };

        if(jNoseCore == null) {
            jNoseCore = new JNoseCore(conf, Runtime.getRuntime().availableProcessors() * 2);
        }

        return jNoseCore;
    }

    static {
        jNoseCore = getInstanceJNoseCore();
    }

    public JNose(){
        jNoseCore = getInstanceJNoseCore();
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


    public static TestClass.JunitVersion getJUnitVersion(String directoryPath) {
        return getInstanceJNoseCore().getJUnitVersion(directoryPath);
    }


    public static List<List<String>> processarTestSmellDetector2(String pathProjeto, StringBuffer logRetorno) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno.insert(0,Util.dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector novo</font> <br>");
        List<List<String>> todasLinhas = new ArrayList<>();

        try {
            List<TestClass> listFileTests = getInstanceJNoseCore().getFilesTest(pathProjeto);
            List<String> linhacolunas = new ArrayList<>();
            linhacolunas.add("App");
            linhacolunas.add("TestFileName");
            linhacolunas.add("ProductionFileName");
            linhacolunas.add("LOC");
            linhacolunas.add("numberMethods");
            listFileTests.get(0).getLineSumTestSmells().keySet().stream().forEach(v -> linhacolunas.add(v));
            todasLinhas.add(linhacolunas);

            for(TestClass testClass : listFileTests){
                List<String> linha = new ArrayList<>();
                linha.add(testClass.getProjectName());
                linha.add(testClass.getName());
                linha.add(testClass.getProductionFile());
                linha.add(testClass.getNumberLine().toString());
                linha.add(testClass.getNumberMethods().toString());
                testClass.getLineSumTestSmells().values().stream().forEach(v -> linha.add(v.toString()));
                todasLinhas.add(linha);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return todasLinhas;
    }


    public static List<TestClass> processarProjeto(ProjetoDTO projeto) throws Exception {
        projeto.setProcentagem(25);
        List<TestClass> listaTestClass = getInstanceJNoseCore().getFilesTest(projeto.getPath());
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

    public static void processarProjetos(List<ProjetoDTO> lista, String folderTime,String pastaPathReport, TotalProcessado totalProcessado) {

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
                List<TestClass> todasLinhas = JNose.processarProjeto(projeto);
                projeto.setResultadoByTestSmells(todasLinhas);
                projeto.setResultado(JNose.convert(todasLinhas));
                listaTestClass.addAll(todasLinhas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void processarEvolution(ProjetoDTO projeto, StringBuffer logRetorno, Map<Integer, List<List<String>>> mapa) {

        GitCore.checkout("master", projeto.getPath());

        List<Commit> lista = new ArrayList<>();

        if (projeto.getOptionSelected().equalsIgnoreCase("commit")) {
            lista = projeto.getListaCommits();
        } else if (projeto.getOptionSelected().equalsIgnoreCase("tag")){
            lista = projeto.getListaTags();
        }

        Collections.sort(lista, new Comparator<Commit>() {
            public int compare(Commit o1, Commit o2) {
                if (o1.date == null || o2.date == null) return 0;
                return o1.date.compareTo(o2.date);
            }
        });

        List<List<String>> todasLinhas1 = new ArrayList<>();
        List<List<String>> todasLinhas2 = new ArrayList<>();
        List<List<String>> todasLinhas3 = new ArrayList<>();

        //lista de commits/testsmells/códigoSHA5
        List<List<String>> todasLinhas4 = new ArrayList<>();
        List<List<String>> todasLinhas5 = new ArrayList<>();


        int cont = 1;

        boolean primeiraLinha = true;

        List<String> jaProcessado = new ArrayList<>();

        //Para cada commit executa uma busca
        for (Commit commit : lista) {

            logRetorno.insert(0,cont++ + " - Analyze commit: " + commit.id + "<br>");

            GitCore.checkout(commit.id, projeto.getPath());

            int total = 0;

            List<TestClass> listTestClass = new ArrayList<>();

            int totalTestSmells = 0;

            try {

                listTestClass = getInstanceJNoseCore().getFilesTest(projeto.getPath());

                if(listTestClass.size() != 0 && primeiraLinha) {
                    primeiraLinha = false;
                    List<String> listaColumName = new ArrayList<>();
                    listaColumName.add("commit.id");
                    listaColumName.add("commit.name");
                    listaColumName.add("commit.date");
                    listaColumName.add("commit.msg");
                    listaColumName.add("commit.tag");
                    listaColumName.add("project");
                    listaColumName.add("TestClass");
                    listaColumName.add("ProductionFile");
                    listaColumName.add("NumberLine");
                    listaColumName.add("NumberMethods");
                    listTestClass.get(0).getLineSumTestSmells().keySet().stream().forEach(v -> listaColumName.add(v));
                    todasLinhas1.add(listaColumName);
                }


                for (TestClass testClass : listTestClass){

//                    totalTestSmells += testClass.getListTestSmell().size();

                    List<String> lista3 = new ArrayList<>();
                    lista3.add(commit.id);
                    lista3.add(commit.name);
                    lista3.add(commit.date.toString());
                    lista3.add(commit.msg);
                    lista3.add(commit.tag);
                    lista3.add(projeto.getName());
                    lista3.add(testClass.getName());
                    lista3.add(testClass.getProductionFile());
                    lista3.add(testClass.getNumberLine().toString());
                    lista3.add(testClass.getNumberMethods().toString());
                    testClass.getLineSumTestSmells().values().stream().forEach(v -> lista3.add(v.toString()));
                    todasLinhas1.add(lista3);

                    for(TestSmell ts : testClass.getListTestSmell()){

                        String sha256 = Util.getSHA5Code(testClass,ts).trim();

                        if(jaProcessado.contains(sha256) == false) {
                            jaProcessado.add(sha256);

                            totalTestSmells++;

                            List<String> lista4 = new ArrayList<>();
                            lista4.add(commit.id);
                            lista4.add(commit.name);
                            lista4.add(commit.date.toString());
                            lista4.add(commit.msg);
                            lista4.add(commit.tag);
                            lista4.add(projeto.getName());
                            lista4.add(testClass.getName());
                            lista4.add(testClass.getProductionFile());
                            lista4.add(testClass.getNumberLine().toString());
                            lista4.add(testClass.getNumberMethods().toString());
                            lista4.add(ts.getName());
                            lista4.add(ts.getMethod());
                            lista4.add(ts.getRange());
                            lista4.add(sha256);
//                            lista4.add(Util.getCode(testClass,ts));
                            todasLinhas3.add(lista4);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            List<String> lista2 = new ArrayList<>();
            lista2.add(commit.id);
            lista2.add(commit.tag);
            lista2.add(commit.date + "");
            lista2.add(totalTestSmells + "");
            todasLinhas2.add(lista2);

        }

        mapa.put(1, todasLinhas1);
        mapa.put(2, todasLinhas2);
        mapa.put(3, todasLinhas3);


        Set<String> setSHA = new HashSet<>();

        Map<String,Integer> mapName = new HashMap<>();

        for(List<String> linha : todasLinhas3){

            if(!setSHA.contains(linha.get(13))){
                setSHA.add(linha.get(13));
                todasLinhas4.add(linha);
            }

            if(!mapName.containsKey(linha.get(1))){
                mapName.put(linha.get(1),1);
            }else{
                mapName.put(linha.get(1),mapName.get(linha.get(1))+1);
            }

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
    }


}

