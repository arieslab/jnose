package br.ufba.jnose.pages;

import br.ufba.jnose.JNoseUtils;
import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.util.ResultsWriter;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
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

    private String pathReport = "";

    private String pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");

    final private Boolean testSmellsTask = false;

    private Integer cont = 0;

    private String projetoPath;

    final private Label taLogInfo;

    final private Label projetoName;

    final private Label projetoCommits;

    final private Label commitsProcessados;

    final private Label csvLogGit;

    private Projeto projetoSelecionado;

    private String logRetornoInfo = "";

    final private TextField tfPastaPath;

    private String pathCSV;

    private static final List<String> TYPES = Arrays
            .asList(new String[] { "Commits", "Tags"});

    private String selected = "Commits";

    public EvolutionPage() {

        pathReport = pathAppToWebapp + File.separator + "reports" + File.separator + "revolution";

        Form form = new Form("form");

        form.add(new AjaxSubmitLink("carregarProjetoLnk") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                projetoSelecionado = carregarProjeto(projetoPath, target);
            }
        });

        RadioChoice<String> radioCommitsTags = new RadioChoice<String>(
                "radioCommitsTags", new PropertyModel<String>(this, "selected"), TYPES);
        form.add(radioCommitsTags);

        projetoName = new Label("projetoName", "");
        projetoName.setOutputMarkupId(true);
        form.add(projetoName);

        projetoCommits = new Label("projetoCommits", "");
        projetoCommits.setOutputMarkupId(true);
        form.add(projetoCommits);

        taLogInfo = new Label("taLogInfo");
        taLogInfo.setEscapeModelStrings(false);
        taLogInfo.setOutputMarkupId(true);
        taLogInfo.setOutputMarkupPlaceholderTag(true);
        add(taLogInfo);

        commitsProcessados = new Label("commitsProcessados", PropertyModel.of(this, "cont"));
        commitsProcessados.setEscapeModelStrings(false);
        commitsProcessados.setOutputMarkupId(true);
        commitsProcessados.setOutputMarkupPlaceholderTag(true);
        form.add(commitsProcessados);

        csvLogGit = new Label("csvLogGit", PropertyModel.of(this, "pathCSV"));
        csvLogGit.setEscapeModelStrings(false);
        csvLogGit.setOutputMarkupId(true);
        csvLogGit.setOutputMarkupPlaceholderTag(true);
        form.add(csvLogGit);

        tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "projetoPath"));
        tfPastaPath.setEscapeModelStrings(false);
        tfPastaPath.setOutputMarkupId(true);
        tfPastaPath.setOutputMarkupPlaceholderTag(true);
        form.add(tfPastaPath);

        IndicatingAjaxLink executarLnk = new IndicatingAjaxLink<String>("executarLnk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                processar(projetoSelecionado, target);
            }
        };
        form.add(executarLnk);

        add(form);


        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                System.out.println("Timer funcionando...");

                System.out.println("logRetornoInfo: " + logRetornoInfo);
                taLogInfo.setDefaultModel(Model.of(logRetornoInfo));
                target.add(taLogInfo);

                System.out.println("commits processados: " + cont);
//                Label commitsProcessados = new Label()

                commitsProcessados.setDefaultModelObject(cont);
                target.add(commitsProcessados);
            }
        };
        add(timer);
    }

    private Projeto carregarProjeto(String pathProjeto, AjaxRequestTarget target) {
//        File filePathProjeto = new File(pathProjeto);
        String preSplit = pathProjeto.replace(File.separator,"/");
        String[] listas = preSplit.split("/");
        String nomeProjeto = listas[listas.length - 1];
        Projeto projeto = new Projeto(nomeProjeto, pathProjeto);
        execCommand("git checkout master", projeto.getPath());

        ArrayList<Commit> lista = null;
        if(selected.trim().equals("Commits")) {
            lista = gitLogOneLine(projeto.getPath());
            projeto.setListaCommits(lista);
        }else{
            lista = gitTags(projeto.getPath());
            projeto.setListaCommits(lista);
        }

        projeto.setName(projeto.getName());
        projeto.setPath(projeto.getPath());
        projeto.setCommits(lista.size());
        projetoName.setDefaultModelObject(projeto.getName());
        target.add(projetoName);
        projetoCommits.setDefaultModelObject(projeto.getCommits());
        target.add(projetoCommits);
        return projeto;
    }


    private void processar(Projeto projeto, AjaxRequestTarget target) {

        List<Commit> lista = projeto.getListaCommits();

        Collections.sort(lista, new Comparator<Commit>() {
            public int compare(Commit o1, Commit o2) {
                if (o1.date == null || o2.date == null) return 0;
                return o2.date.compareTo(o1.date);
            }
        });

        ResultsWriter resultsWriter = null;
        if (true) {
            String reportPathFinal = pathReport + File.separatorChar + dateNow() + File.separatorChar;
            boolean success = (new File(reportPathFinal)).mkdirs();
            if (!success) System.out.println("Pasta Criada...");
            String csvTestSmells = reportPathFinal + projeto.getName() + "_testsmesll.csv";
            try {
                resultsWriter = ResultsWriter.createResultsWriter(csvTestSmells);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Para cada commit executa uma busca
        for (Commit commit : projeto.getListaCommits()) {
            cont++;

            commitsProcessados.setDefaultModel(Model.of(cont));
            target.add(commitsProcessados);

            System.out.println("cont: " + cont);

            logRetornoInfo = "Indo para -> " + commit.id + "<br>" + logRetornoInfo;
            execCommand("git checkout " + commit.id, projetoPath);


            //criando a lista de testsmells
            List<String[]> listaTestSmells = processarTestSmells(projetoPath, commit, pathReport, true);
            for (String[] linhaArray : listaTestSmells) {
                List<String> list = Arrays.asList(linhaArray);
                try {
                    resultsWriter.writeLine(list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("arquivo final -> " + resultsWriter.getOutputFile());
            csvLogGit.setDefaultModelObject(resultsWriter.getOutputFile());
            target.add(csvLogGit);
        }
        execCommand("git checkout master", projetoPath);
    }

    private void execCommand(final String commandLine, String pathExecute) {
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

    private ArrayList<Commit> gitLogOneLine(String pathExecute) {
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

    private ArrayList<Commit> gitTags(String pathExecute) {
        ArrayList<Commit> lista = new ArrayList<>();
        int r = 0;
        try {
            //b8f638fa,Gary Gregory,2019-08-09,Javadoc.
            Process p = Runtime.getRuntime().exec("git tag", null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                String tagName = lineOut.trim();
                Process detalhes = Runtime.getRuntime().exec("git show " + tagName, null, new File(pathExecute));
                BufferedReader input2 = new BufferedReader(new InputStreamReader(detalhes.getInputStream()));
                String commit = "";
                String lineOut2;

                String id = "";
                String name = "";
                Date date = null;
                String msg = "";

                while ((lineOut2 = input2.readLine()) != null) {
                    System.out.println(lineOut2);

                    if(lineOut2.trim().contains("Tagger:")){
                        name = lineOut2.trim().replace("Tagger:","").trim();
                    }

                    if(lineOut2.trim().contains("Date:")){
                        String dateString = lineOut2.trim().replace("Date:","").trim();
//                        dateString = dateString.replace("+0000","").trim();
                        SimpleDateFormat formatter5 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z",Locale.US);
                        System.out.println(formatter5.format(new Date()));

                        date = formatter5.parse(dateString);
                    }

                    if(lineOut2.trim().contains("commit ")){
                        id = lineOut2.trim().replace("commit ","").trim();
                    }


                }

                lista.add(new Commit(id, name, date, msg));
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private String dateNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private List<String[]> processarTestSmells(String pathProjeto, Commit commit, String pastaPathReport, Boolean cabecalho) {
        List<String[]> listTestSmells = null;
        try {
            System.out.println("TestSmells: " + pathProjeto + " - " + pastaPathReport);
            List<TestClass> listTestFile = JNoseUtils.getFilesTest(pathProjeto);
            List<String[]> listMapping = processarTestFileMapping(listTestFile, commit, pathProjeto);
            listTestSmells = processarTestSmellDetector(listMapping, cabecalho);
        }catch (Exception e){
            e.printStackTrace();
        }
        return listTestSmells;
    }

    private List<String[]> processarTestFileMapping(List<TestClass> listTestClass, Commit commit, String pathProjeto) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separator) + 1, pathProjeto.length());
        List<String[]> listaResultado = new ArrayList<>();
        try {
            listaResultado = JNoseUtils.testfilemapping(listTestClass,commit, pathProjeto, nameProjeto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaResultado;
    }

    private static List<String[]> processarTestSmellDetector(List<String[]> listMapping, Boolean cabecalho) {
        List<String[]> lista = null;
        try {
            return br.ufba.jnose.core.testsmelldetector.Main.start(listMapping, cabecalho);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

}