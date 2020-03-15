package br.ufba.jnose.pages;

import br.ufba.jnose.core.evolution.Commit;
import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.util.ResultsWriter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EvolutionPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String pathReport = "/home/tassio/Desenvolvimento/repo.git/jnose/report/revolution";

    private Boolean testSmellsTask = false;

    private int total = 0;

    private int cont = 0;

    private String projetoPath;

    private Label taLogInfo;

    private Label projetoName;

    private Label projetoCommits;

    private Label commitsProcessados;

    public String logRetornoInfo = "";

    public Projeto projetoSelecionado;

    private Integer qtdCommitsProcessados = 0;

    public EvolutionPage() {

        projetoName = new Label("projetoName", "");
        projetoName.setEscapeModelStrings(false);
        projetoName.setOutputMarkupId(true);
        projetoName.setOutputMarkupPlaceholderTag(true);
        add(projetoName);

        projetoCommits = new Label("projetoCommits", "");
        projetoCommits.setEscapeModelStrings(false);
        projetoCommits.setOutputMarkupId(true);
        projetoCommits.setOutputMarkupPlaceholderTag(true);
        add(projetoCommits);

        commitsProcessados = new Label("commitsProcessados", "");
        commitsProcessados.setEscapeModelStrings(false);
        commitsProcessados.setOutputMarkupId(true);
        commitsProcessados.setOutputMarkupPlaceholderTag(true);
        add(commitsProcessados);


        taLogInfo = new Label("taLogInfo");
        taLogInfo.setEscapeModelStrings(false);
        taLogInfo.setOutputMarkupId(true);
        taLogInfo.setOutputMarkupPlaceholderTag(true);
        add(taLogInfo);


        Form form = new Form<>("form");
        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "projetoPath"));
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                projetoSelecionado = carregarProjeto(projetoPath);
            }
        };
        form.add(btEnviar);
        add(form);

        Form form2 = new Form<>("form2");
        Button btEnviar2 = new Button("btEnviar2") {
            @Override
            public void onSubmit() {
                try {
                    processar(projetoSelecionado,commitsProcessados);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        form2.add(btEnviar2);
        add(form2);


        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                taLogInfo.setDefaultModel(Model.of(logRetornoInfo));
                target.add(taLogInfo);

                commitsProcessados.setDefaultModel(Model.of(qtdCommitsProcessados));
                target.add(commitsProcessados);
            }
        };
        add(timer);
    }

    private Projeto carregarProjeto(String pathProjeto) {

        String[] listas = pathProjeto.split("/");

        String nomeProjeto = listas[listas.length-1];

        Projeto projeto = new Projeto(nomeProjeto,pathProjeto);

        execCommand("git checkout master", projeto.getPath());

        ArrayList<Commit> lista = gitLogOneLine(projeto.getPath());

        projeto.setListaCommits(lista);
        projeto.setName(projeto.getName());
        projeto.setPath(projeto.getPath());
        projeto.setCommits(lista.size());

        projetoName.setDefaultModelObject(projeto.getName());
        projetoCommits.setDefaultModelObject(projeto.getCommits());

        return projeto;
    }


    private void processar(Projeto projeto, Label commitsProcessados) throws Exception {

        List<Commit> lista = projeto.getListaCommits();

        //ULTIMO -> PRIMEIRO
        Collections.sort(lista, new Comparator<Commit>() {
            public int compare(Commit o1, Commit o2) {
                if (o1.date == null || o2.date == null) return 0;
                return o2.date.compareTo(o1.date);
            }
        });

        ResultsWriter resultsWriter = null;
        if (testSmellsTask) {
            String reportPathFinal = pathReport + "/" + dateNow() + "/";
            boolean success = (new File(reportPathFinal)).mkdirs();
            if (!success) System.out.println("Pasta Criada...");
            String nameProjeto = projetoPath.substring(projetoPath.lastIndexOf("/") + 1, projetoPath.length());
            String csvTestSmells = reportPathFinal + nameProjeto + "_testsmesll.csv";
            resultsWriter = ResultsWriter.createResultsWriter(csvTestSmells);
        }

        //Para cada commit executa uma busca
        for (Commit commit : projeto.getListaCommits()) {
            cont++;

            qtdCommitsProcessados = cont;

            logRetornoInfo = "Indo para -> " + commit.id + "<br>" + logRetornoInfo;
            execCommand("git checkout " + commit.id, projetoPath);
            Thread.sleep(5);

            boolean cabecalho = true;

            //criando a lista de testsmells
            if (testSmellsTask) {
                List<String[]> listaTestSmells = processarTestSmells(projetoPath, commit, pathReport, cabecalho);
                for (String[] linhaArray : listaTestSmells) {
                    List<String> list = Arrays.asList(linhaArray);
                    resultsWriter.writeLine(list);
                }
                cabecalho = false;
                System.out.println("arquivo final -> " + resultsWriter.getOutputFile());
            }

        }
    }

    private static void execCommand(final String commandLine, String pathExecute) {
        int r = 0;
        try {
            Process p = Runtime.getRuntime().exec(commandLine, null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                System.out.println(lineOut);
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Commit> gitLogOneLine(String pathExecute) {
        ArrayList<Commit> lista = new ArrayList<>();
        int r = 0;
        try {
            //b8f638fa,Gary Gregory,2019-08-09,Javadoc.
            Process p = Runtime.getRuntime().exec("git log --pretty=format:%h,%an,%ad,%s --date=iso8601", null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                String[] arrayCommit = lineOut.split(",");
                String id = arrayCommit[0];
                String name = arrayCommit[1];
                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(arrayCommit[2]);
                String msg = arrayCommit[3];
                lista.add(new Commit(id, name, date, msg));
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private static String dateNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private static List<String[]> processarTestSmells(String pathProjeto, Commit commit, String pastaPathReport, Boolean cabecalho) throws IOException {
        System.out.println("TestSmells: " + pathProjeto + " - " + pastaPathReport);
        List<String[]> listTestFile = processarTestFileDetector(pathProjeto, commit);
        List<String[]> listMapping = processarTestFileMapping(listTestFile, pathProjeto);
        List<String[]> listTestSmells = processarTestSmellDetector(listMapping, cabecalho);
        return listTestSmells;
    }

    private static List<String[]> processarTestFileDetector(String pathProjeto, Commit commit) throws IOException {
        return br.ufba.jnose.core.testfiledetector.Main.start(pathProjeto, commit);
    }

    private static List<String[]> processarTestFileMapping(List<String[]> listTestFile, String pathProjeto) throws IOException {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/") + 1, pathProjeto.length());
        List<String[]> listaResultado = new ArrayList<>();
        listaResultado = br.ufba.jnose.core.testfilemapping.Main.start(listTestFile, pathProjeto, nameProjeto);
        return listaResultado;
    }

    private static List<String[]> processarTestSmellDetector(List<String[]> listMapping, Boolean cabecalho) throws IOException {
        return br.ufba.jnose.core.testsmelldetector.Main.start(listMapping, cabecalho);
    }

}