package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxPreventSubmitBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import java.io.File;
import java.util.*;

public class EvolutionPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private Label taLogInfo;
    private StringBuffer logRetorno;
    private List<Projeto> listaProjetos;
    private ListView<Projeto> lvProjetos;

    public EvolutionPage() {
        super("EvolutionPage");
        logRetorno = new StringBuffer();
        criarTimer();
        criarListaProjetos();
        criarLogInfo();
        loadProjetos();
    }

    private void loadProjetos() {
        File file = new File(WicketApplication.JNOSE_PROJECTS_FOLDER);
        listaProjetos = JNoseCore.listaProjetos(file.toURI(), logRetorno);
        lvProjetos.setList(listaProjetos);
    }

    private void criarLogInfo() {
        taLogInfo = new Label("taLogInfo", Model.of(logRetorno));
        taLogInfo.setEscapeModelStrings(false);
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

                for(Projeto projeto:listaProjetos){
                    if(projeto.getMapResults().containsKey(1)){
                        projeto.lkResult1.setEnabled(true);
                        projeto.lkResult1.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult1);
                    }else{
                        projeto.lkResult1.setEnabled(false);
                        projeto.lkResult1.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult1);
                    }
                    if(projeto.getMapResults().containsKey(2)){
                        projeto.lkResult2.setEnabled(true);
                        projeto.lkResult2.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult2);
                    }else{
                        projeto.lkResult2.setEnabled(false);
                        projeto.lkResult2.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult2);
                    }
                }

            }
        };
        add(timer);
    }


    private void criarListaProjetos() {
        lvProjetos = new ListView<Projeto>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {

                Projeto projeto = item.getModelObject();

                Map<Integer, List<List<String>>> mapResults = new HashMap<>();
                projeto.setMapResults(mapResults);
                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("path", projeto.getPath()));
                item.add(new Label("branch", GitCore.branch(projeto.getPath())));
                projeto.setListaCommits(GitCore.gitLogOneLine(projeto.getPath()));
                projeto.setListaTags(GitCore.gitTags(projeto.getPath()));

                Form form = new Form<String>("form");
                form.setOutputMarkupId(true);
                form.add(new AjaxPreventSubmitBehavior());

                Link lkResult1 = new Link<String>("lkResult1") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas1 = mapResults.get(1);
                        setResponsePage(new ResultPage(todasLinhas1, "Evolution Report 1 - TestSmells by Commit: " + projeto.getName(), "resultado_evolution1", false));
                    }
                };
                lkResult1.setOutputMarkupId(true);
                lkResult1.setOutputMarkupPlaceholderTag(true);
                lkResult1.setEnabled(false);
                lkResult1.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                projeto.lkResult1 = lkResult1;
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
                lkResult2.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                projeto.lkResult2 = lkResult2;
                form.add(lkResult2);


                IndicatingAjaxLink btSubmit = new IndicatingAjaxLink<String>("btSubmit") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        System.out.println("Processamento do projeto: " + projeto.getName() + " - Start");
                        logRetorno.insert(0,"Processamento do projeto: " + projeto.getName() + " - Start<br>");

                        new Thread() { // IMPORTANTE: AQUI SE CRIA AS THREADS
                            @Override
                            public void run() {
                                JNoseCore.processarEvolution(projeto, logRetorno, projeto.getMapResults());
                            }
                        }.start();

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

}