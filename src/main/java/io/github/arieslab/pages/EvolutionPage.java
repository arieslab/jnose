package io.github.arieslab.pages;

import io.github.arieslab.business.ProjetoBusiness;
import io.github.arieslab.base.GitCore;
import io.github.arieslab.base.JNose;
import io.github.arieslab.dtolocal.ProjetoDTO;
import io.github.arieslab.entities.Projeto;
import io.github.arieslab.pages.base.BasePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxPreventSubmitBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvolutionPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(EvolutionPage.class.getName());

    private Label taLogInfo;
    private StringBuffer logRetorno;
    private List<ProjetoDTO> listaProjetos;
    private ListView<ProjetoDTO> lvProjetos;
    private transient ExecutorService evolutionExecutor;

    @SpringBean
    private ProjetoBusiness projetoBusiness;

    public EvolutionPage() {
        super("EvolutionPage");
        listaProjetos = new ArrayList<>();
        logRetorno = new StringBuffer();
        evolutionExecutor = Executors.newSingleThreadExecutor();
        criarTimer();
        criarListaProjetos();
        criarLogInfo();
        loadProjetos();
    }

    private void loadProjetos() {
        List<Projeto> listProjetoBean = projetoBusiness.listAllWithFilter();
        for(Projeto projeto : listProjetoBean){
            listaProjetos.add(new ProjetoDTO(projeto));
        }
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
        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(java.time.Duration.ofSeconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                taLogInfo.setDefaultModelObject(logRetorno);
                target.add(taLogInfo);

                for(ProjetoDTO projeto:listaProjetos){
                    if(projeto.getMapResults() != null && projeto.getMapResults().containsKey(1)){
                        projeto.lkResult1.setEnabled(true);
                        projeto.lkResult1.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult1);
                    }else{
                        projeto.lkResult1.setEnabled(false);
                        projeto.lkResult1.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult1);
                    }
                    if(projeto.getMapResults() != null && projeto.getMapResults().containsKey(2)){
                        projeto.lkResult2.setEnabled(true);
                        projeto.lkResult2.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult2);
                    }else{
                        projeto.lkResult2.setEnabled(false);
                        projeto.lkResult2.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult2);
                    }
                    if(projeto.getMapResults() != null && projeto.getMapResults().containsKey(3)){
                        projeto.lkResult3.setEnabled(true);
                        projeto.lkResult3.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult3);
                    }else{
                        projeto.lkResult3.setEnabled(false);
                        projeto.lkResult3.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult3);
                    }
                    if(projeto.getMapResults() != null && projeto.getMapResults().containsKey(4)){
                        projeto.lkResult4.setEnabled(true);
                        projeto.lkResult4.add(AttributeModifier.remove("style"));
                        target.add(projeto.lkResult4);
                    }else{
                        projeto.lkResult4.setEnabled(false);
                        projeto.lkResult4.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                        target.add(projeto.lkResult4);
                    }
                }

            }
        };
        add(timer);
    }


    private void criarListaProjetos() {
        lvProjetos = new ListView<ProjetoDTO>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<ProjetoDTO> item) {

                ProjetoDTO projeto = item.getModelObject();

                Map<Integer, List<List<String>>> mapResults = new ConcurrentHashMap<>();
                projeto.setMapResults(mapResults);
                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("path", projeto.getPath()));

                if (Files.exists(Path.of(projeto.getPath()))) {
                    item.add(new Label("branch", GitCore.branch(projeto.getPath())));
                    projeto.setListaCommits(GitCore.gitLogOneLine(projeto.getPath()));
                    projeto.setListaTags(GitCore.gitTags(projeto.getPath()));
                } else {
                    item.add(new Label("branch", "Diretório não encontrado"));
                    projeto.setListaCommits(new ArrayList<>());
                    projeto.setListaTags(new ArrayList<>());
                }

                Form form = new Form<String>("form");
                form.setOutputMarkupId(true);
                form.add(new AjaxPreventSubmitBehavior());

                Link lkResult1 = new Link<String>("lkResult1") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas1 = mapResults.get(1);
                        setResponsePage(new ResultPage(todasLinhas1, "Evolution Report 1 - TestSmells by Commit and Class: " + projeto.getName(), "resultado_evolution1", false));
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

                Link lkResult3 = new Link<String>("lkResult3") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas3 = mapResults.get(3);
                        setResponsePage(new ResultPage(todasLinhas3, "Evolution Report 3 - Testsmells Detail by Commit: " + projeto.getName(), "resultado_evolution3", false));

                    }
                };
                lkResult3.setOutputMarkupId(true);
                lkResult3.setOutputMarkupPlaceholderTag(true);
                lkResult3.setEnabled(false);
                lkResult3.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                projeto.lkResult3 = lkResult3;
                form.add(lkResult3);

                Link lkResult4 = new Link<String>("lkResult4") {
                    @Override
                    public void onClick() {
                        List<List<String>> todasLinhas4 = mapResults.get(4);
                        setResponsePage(new ResultPage(todasLinhas4, "Evolution Report 4 - Unique Testsmells: " + projeto.getName(), "resultado_evolution4", false));

                    }
                };
                lkResult4.setOutputMarkupId(true);
                lkResult4.setOutputMarkupPlaceholderTag(true);
                lkResult4.setEnabled(false);
                lkResult4.add(AttributeModifier.append("style","background-color: #e0e0eb;"));
                projeto.lkResult4 = lkResult4;
                form.add(lkResult4);

                AjaxLink btSubmit = new AjaxLink<String>("btSubmit") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        LOGGER.log(Level.INFO, "Processamento do projeto: {0} - Start", projeto.getName());
                        logRetorno.insert(0,"Processamento do projeto: " + projeto.getName() + " - Start<br>");
                        evolutionExecutor.submit(() ->
                                JNose.processarEvolution(projeto, logRetorno, projeto.getMapResults())
                        );
                    }
                };
                btSubmit.setEnabled(false);
                form.add(btSubmit);


                RadioGroup radioCommitsTags = new RadioGroup("radioCommitsTags", PropertyModel.of (projeto, "optionSelected"));

                Radio radio1 = new Radio("commit",new Model<String>("commit"));
                radio1.add(new AjaxEventBehavior("change") {
                    protected void onEvent(AjaxRequestTarget target) {
                        projeto.setOptionSelected("commit");
                        btSubmit.setEnabled(true);
                        target.add(btSubmit);
                    }
                });
                radioCommitsTags.add(radio1);
                radioCommitsTags.add(new Label("ck1", Model.of(projeto.getListaCommits().size())));

                Radio radio2 = new Radio("tag",new Model<String>("tag"));
                radio2.add(new AjaxEventBehavior("change") {
                    protected void onEvent(AjaxRequestTarget target) {
                        projeto.setOptionSelected("tag");
                        btSubmit.setEnabled(true);
                        target.add(btSubmit);
                    }
                });
                radioCommitsTags.add(radio2);
                radioCommitsTags.add(new Label("ck2", Model.of(projeto.getListaTags().size())));
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
