package br.ufba.jnose.pages;

import br.ufba.jnose.core.CSVCore;
import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
    private StringBuffer logRetorno;
    private String projetoPath;
    private String pathCSV;
    private String selected;
    private String pathReport;
    private String pathAppToWebapp;
    private Integer cont;
    private List<Projeto> listaProjetos;
    private ListView<Projeto> lvProjetos;

    public EvolutionPage() {
        this(null);
    }

    public EvolutionPage(Projeto projeto) {
        super("EvolutionPage");

        projetoSelecionado = projeto;
        logRetorno = new StringBuffer();
        projetoPath = "";
        pathCSV = "";
        selected = "Commits";
        pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
        pathReport = "";
        cont = 0;

        pathReport = pathAppToWebapp + File.separator + "reports" + File.separator + "revolution";

        add(new JQueryFeedbackPanel("feedback").setOutputMarkupId(true));

        criarListaProjetos();
        criarForm();
        criarLogInfo();
        criarTimer();
        loadProjetos();
    }

    private void loadProjetos(){
        File file = new File("./projects");
        listaProjetos = JNoseCore.listaProjetos(file.toURI(),logRetorno);
        lvProjetos.setList(listaProjetos);
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
                taLogInfo.setDefaultModel(Model.of(logRetorno));
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
        tfPastaPath.setEnabled(false);
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

    private void criarListaProjetos(){
        lvProjetos = new ListView<Projeto>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {
                Projeto projeto = item.getModelObject();
                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("path", projeto.getPath()));

                AjaxLink lkSelect = new AjaxLink<Void>("lkSelect") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
//                        projetoPath = projeto.getPath();
//                        projetoSelecionado = carregarProjeto(projetoPath, ajaxRequestTarget);
                        tfPastaPath.setModelObject(projeto.getPath());
                        ajaxRequestTarget.add(tfPastaPath);
                    }
                };

                item.add(lkSelect);
            }
        };
        lvProjetos.setOutputMarkupId(true);
        lvProjetos.setOutputMarkupPlaceholderTag(true);
        add(lvProjetos);
    }

    private Projeto carregarProjeto(String pathProjeto, AjaxRequestTarget target) {

        String preSplit = pathProjeto.replace(File.separator, "/");
        String[] listas = preSplit.split("/");
        String nomeProjeto = listas[listas.length - 1];
        Projeto projeto = new Projeto(nomeProjeto, pathProjeto);

        GitCore.checkout("master", projeto.getPath());

        ArrayList<Commit> lista = null;
        if (selected.trim().equals("Commits")) {
            lista = GitCore.gitLogOneLine(projeto.getPath());
            projeto.setListaCommits(lista);
        } else {
            lista = GitCore.gitTags(projeto.getPath());
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

        String pastaDateHora = JNoseCore.dateNowFolder();
        List<List<String>> todasLinhas1 = new ArrayList<>();
        List<List<String>> todasLinhas2 = new ArrayList<>();

        boolean vizualizarCabecalho = true;

        //Para cada commit executa uma busca
        for (Commit commit : projeto.getListaCommits()) {
            cont++;
            commitsProcessados.setDefaultModel(Model.of(cont));
            target.add(commitsProcessados);

            GitCore.checkout(commit.id, projetoPath);

            int total = 0;
            //criando a lista de testsmells
            List<String[]> listaTestSmells = JNoseCore.processarTestSmells(projetoPath, commit, vizualizarCabecalho,logRetorno);
            for (String[] linhaArray : listaTestSmells) {
                List<String> list = Arrays.asList(linhaArray);
                    for (int i = 10; i <= (list.size() - 1); i++) {
                        boolean isNumeric = list.get(i).chars().allMatch( Character::isDigit );
                        if(isNumeric) {
                            total += Integer.parseInt(list.get(i));
                        }
                    }
                todasLinhas1.add(list);
            }

            List<String> lista2 = new ArrayList<>();
            lista2.add(commit.id);
            lista2.add(commit.tag);
            lista2.add(commit.date+"");
            lista2.add(total+"");
            todasLinhas2.add(lista2);

            String arquivoPath = CSVCore.criarEvolution1CSV(todasLinhas1,pastaDateHora,projeto.getName());
            CSVCore.criarEvolution2CSV(todasLinhas2,pastaDateHora,projeto.getName());

            csvLogGit.setDefaultModelObject(arquivoPath);
            target.add(csvLogGit);

            vizualizarCabecalho = false;
        }
        GitCore.checkout("master", projetoPath);
    }

}