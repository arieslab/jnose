package br.ufba.jnose.pages;

import br.ufba.jnose.util.JNoseUtils;
import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.util.ResultsWriter;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
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

import java.io.File;
import java.util.*;

public class EvolutionPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private Label taLogInfo;
    private Label projetoName;
    private Label projetoCommits;
    private Label commitsProcessados;
    private Label csvLogGit;
    private TextField tfPastaPath;

    private Projeto projetoSelecionado;
    private StringBuffer logRetornoInfo;
    private String projetoPath;
    private String pathCSV;
    private String selected;
    private String pathReport;
    private String pathAppToWebapp;
    private Integer cont;

    public EvolutionPage() {
        logRetornoInfo = new StringBuffer();
        projetoPath = "";
        pathCSV = "";
        selected = "Commits";
        pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
        pathReport = "";
        cont = 0;

        pathReport = pathAppToWebapp + File.separator + "reports" + File.separator + "revolution";

        add(new JQueryFeedbackPanel("feedback").setOutputMarkupId(true));

        criarForm();
        criarLogInfo();
        criarTimer();
    }

    private void criarLogInfo(){
        taLogInfo = new Label("taLogInfo");
        taLogInfo.setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(taLogInfo);
    }

    private void criarTimer(){
        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                taLogInfo.setDefaultModel(Model.of(logRetornoInfo));
                target.add(taLogInfo);
                commitsProcessados.setDefaultModelObject(cont);
                target.add(commitsProcessados);
            }
        };
        add(timer);
    }

    private void criarForm(){
        Form form = new Form("form");
        form.add(new AjaxSubmitLink("carregarProjetoLnk") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                projetoSelecionado = carregarProjeto(projetoPath, target);
            }
        });

        RadioChoice<String> radioCommitsTags = new RadioChoice<String>(
                "radioCommitsTags", new PropertyModel<String>(this, "selected"), Arrays.asList(new String[]{"Commits", "Tags"}));
        radioCommitsTags.setPrefix(" ");
        radioCommitsTags.setSuffix("<br>");
        form.add(radioCommitsTags);

        projetoName = new Label("projetoName", "");
        form.add(projetoName.setOutputMarkupId(true));

        projetoCommits = new Label("projetoCommits", "");
        form.add(projetoCommits.setOutputMarkupId(true));

        commitsProcessados = new Label("commitsProcessados", PropertyModel.of(this, "cont"));
        commitsProcessados.setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        form.add(commitsProcessados);

        csvLogGit = new Label("csvLogGit", PropertyModel.of(this, "pathCSV"));
        csvLogGit.setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        form.add(csvLogGit);

        tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "projetoPath"));
        tfPastaPath.setRequired(true).setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        form.add(tfPastaPath);

        IndicatingAjaxLink executarLnk = new IndicatingAjaxLink<String>("executarLnk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                processar(projetoSelecionado, target);
            }
        };
        form.add(executarLnk);
        add(form);
    }

    private Projeto carregarProjeto(String pathProjeto, AjaxRequestTarget target) {

        String preSplit = pathProjeto.replace(File.separator, "/");
        String[] listas = preSplit.split("/");
        String nomeProjeto = listas[listas.length - 1];
        Projeto projeto = new Projeto(nomeProjeto, pathProjeto);
        JNoseUtils.execCommand("git checkout master", projeto.getPath());

        ArrayList<Commit> lista = null;
        if (selected.trim().equals("Commits")) {
            lista = JNoseUtils.gitLogOneLine(projeto.getPath());
            projeto.setListaCommits(lista);
        } else {
            lista = JNoseUtils.gitTags(projeto.getPath());
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

        String reportPathFinal = pathReport + File.separatorChar + JNoseUtils.dateNow() + File.separatorChar;

        boolean success = (new File(reportPathFinal)).mkdirs();
        if (!success) System.out.println("Created Folder...");

        String csvTestSmells = reportPathFinal + projeto.getName() + "_testsmesll.csv";
        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(csvTestSmells);

        String csvTestSmells2 = reportPathFinal + projeto.getName() + "_testsmesll-evolution_total.csv";
        ResultsWriter resultsWriter2 = ResultsWriter.createResultsWriter(csvTestSmells2);


        boolean vizualizarCabecalho = true;

        //Para cada commit executa uma busca
        for (Commit commit : projeto.getListaCommits()) {
            cont++;
            commitsProcessados.setDefaultModel(Model.of(cont));
            target.add(commitsProcessados);
            JNoseUtils.execCommand("git checkout " + commit.id, projetoPath);

            int total = 0;
            //criando a lista de testsmells
            List<String[]> listaTestSmells = JNoseUtils.processarTestSmells(projetoPath, commit, vizualizarCabecalho);
            for (String[] linhaArray : listaTestSmells) {
                List<String> list = Arrays.asList(linhaArray);
                    for (int i = 10; i <= (list.size() - 1); i++) {
                        boolean isNumeric = list.get(i).chars().allMatch( Character::isDigit );
                        if(isNumeric) {
                            total += Integer.parseInt(list.get(i));
                        }
                    }
                resultsWriter.writeLine(list);
            }

            List<String> lista2 = new ArrayList<>();
            lista2.add(commit.id);
            lista2.add(commit.tag);
            lista2.add(commit.date+"");
            lista2.add(total+"");

            resultsWriter2.writeLine(lista2);

            csvLogGit.setDefaultModelObject(resultsWriter.getOutputFile());
            target.add(csvLogGit);

            vizualizarCabecalho = false;
        }
        JNoseUtils.execCommand("git checkout master", projetoPath);
    }

}