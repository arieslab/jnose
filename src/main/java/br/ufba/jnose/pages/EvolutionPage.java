package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.core.Util;
import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
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

    private Projeto projetoSelecionado;
    private StringBuffer logRetorno;
    private String pathCSV;
    public String selected;
    private String pathReport;
    private String pathAppToWebapp;
    private List<Projeto> listaProjetos;
    private ListView<Projeto> lvProjetos;

    public EvolutionPage() {
        this(null);
    }

    public EvolutionPage(Projeto projeto) {
        super("EvolutionPage");

        projetoSelecionado = projeto;
        logRetorno = new StringBuffer();
        pathCSV = "";
        selected = "Commits";
        pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
        pathReport = "";

        pathReport = pathAppToWebapp + File.separator + "reports" + File.separator + "revolution";

        add(new JQueryFeedbackPanel("feedback").setOutputMarkupId(true));

        criarListaProjetos();
        criarLogInfo();
        criarTimer();
        loadProjetos();
    }

    private void loadProjetos() {
        File file = new File(WicketApplication.JNOSE_PROJECTS_FOLDER);
        listaProjetos = JNoseCore.listaProjetos(file.toURI(), logRetorno);
        lvProjetos.setList(listaProjetos);
    }

    private void criarLogInfo() {
        taLogInfo = new Label("taLogInfo", Model.of(logRetorno));
        taLogInfo.setOutputMarkupId(true);
        taLogInfo.setOutputMarkupPlaceholderTag(true);
        add(taLogInfo);
    }

    private void criarTimer() {
        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                taLogInfo.setDefaultModelObject(logRetorno);
                target.add(taLogInfo);
            }
        };
        add(timer);
    }


    private void criarListaProjetos() {
        lvProjetos = new ListView<Projeto>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {

                Map<Integer, List<List<String>>> mapResults = new HashMap<>();

                Projeto projeto = item.getModelObject();
                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("path", projeto.getPath()));
                item.add(new Label("branch", GitCore.branch(projeto.getPath())));
                projeto.setListaCommits(GitCore.gitLogOneLine(projeto.getPath()));
                projeto.setListaTags(GitCore.gitTags(projeto.getPath()));

                Form form = new Form<String>("form");

                final Link lkResult1 = new Link<String>("lkResult1") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas1 = mapResults.get(1);
                        setResponsePage(new ResultPage(todasLinhas1, "Evolution Report 1 - TestSmells by Commit: " + projeto.getName(), "resultado_evolution1", false));

                    }
                };
                lkResult1.setOutputMarkupId(true);
                lkResult1.setOutputMarkupPlaceholderTag(true);
                lkResult1.setEnabled(false);
                form.add(lkResult1);

                Link lkResult2 = new Link<String>("lkResult2") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas2 = mapResults.get(2);
                        setResponsePage(new ResultPage(todasLinhas2, "Evolution Report 2 - Total Testsmells by Commit: " + projeto.getName(), "resultado_evolution2", false));

                    }
                };
                lkResult2.setOutputMarkupId(true);
                lkResult2.setOutputMarkupPlaceholderTag(true);
                lkResult2.setEnabled(false);
                form.add(lkResult2);


                AjaxSubmitLink btSubmit = new AjaxSubmitLink("btSubmit") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        super.onSubmit();
                        System.out.println(projeto);
                        Map<Integer, List<List<String>>> map = processar(projeto, target);
                        mapResults.put(1, map.get(1));
                        mapResults.put(2, map.get(2));
                        System.out.println("Processamento do projeto: " + projeto.getName() + " - Concluído<br>");
                        logRetorno.append("Processamento do projeto: " + projeto.getName() + " - Concluído<br>");
                        taLogInfo.setDefaultModelObject(logRetorno);
                        target.add(taLogInfo);

                        lkResult1.setEnabled(true);
                        lkResult2.setEnabled(true);
                        target.add(lkResult1);
                        target.add(lkResult2);

                    }
                };
                btSubmit.setEnabled(false);
                form.add(btSubmit);

                RadioChoice<String> radioCommitsTags = new RadioChoice<String>(
                        "radioCommitsTags", new PropertyModel<String>(projeto, "optionSelected"),
                        Arrays.asList(new String[]{projeto.getListaCommits().size() + " / ", projeto.getListaTags().size() + ""})) {

                };
                radioCommitsTags.add(new AjaxEventBehavior("change") {
                    protected void onEvent(AjaxRequestTarget target) {
                        btSubmit.setEnabled(true);
                        target.add(btSubmit);
                    }
                });
                radioCommitsTags.setOutputMarkupId(true);
                radioCommitsTags.setOutputMarkupPlaceholderTag(true);
                form.add(radioCommitsTags);
                item.add(form);
            }
        };
        lvProjetos.setOutputMarkupId(true);
        lvProjetos.setOutputMarkupPlaceholderTag(true);
        add(lvProjetos);
    }


    private Map<Integer, List<List<String>>> processar(Projeto projeto, AjaxRequestTarget target) {

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

        String pastaDateHora = Util.dateNowFolder();
        List<List<String>> todasLinhas1 = new ArrayList<>();
        List<List<String>> todasLinhas2 = new ArrayList<>();

        Map mapRetorn = new HashMap();
        mapRetorn.put(1, todasLinhas1);
        mapRetorn.put(2, todasLinhas2);

        boolean vizualizarCabecalho = true;

        //Para cada commit executa uma busca
        for (Commit commit : lista) {

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
                    logRetorno.append("processando...    " + i + "<br>");
                    taLogInfo.setDefaultModelObject(logRetorno);
                    target.add(taLogInfo);
                }
                todasLinhas1.add(list);
            }

            List<String> lista2 = new ArrayList<>();
            lista2.add(commit.id);
            lista2.add(commit.tag);
            lista2.add(commit.date + "");
            lista2.add(total + "");
            todasLinhas2.add(lista2);

//            String arquivoPath = CSVCore.criarEvolution1CSV(todasLinhas1, pastaDateHora, projeto.getName());
//            CSVCore.criarEvolution2CSV(todasLinhas2, pastaDateHora, projeto.getName());

            vizualizarCabecalho = false;
        }
        GitCore.checkout("master", projeto.getPath());

        return mapRetorn;
    }

}