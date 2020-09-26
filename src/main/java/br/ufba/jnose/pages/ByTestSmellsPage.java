package br.ufba.jnose.pages;

import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.dto.TotalProcessado;
import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.core.JNoseCore;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.time.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByTestSmellsPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String pastaPath;
    private String pathAppToWebapp;
    private String pastaPathReport;
    private Label lbPastaSelecionada;
    private ProgressBar progressBar;
    private List<Projeto> listaProjetos;
    private AjaxIndicatorAppender indicator;
    private ListView<Projeto> lvProjetos;
    private TotalProcessado totalProcessado;
    private Map<Integer, Integer> totalProgressBar;
    private Boolean processando;
    private IndicatingAjaxLink processarTodos;
    private Label lbProjetosSize;
    private String dataProcessamentoAtual;
    private ExternalLink linkCSVFinal;
    private StringBuffer logRetorno;

    public ByTestSmellsPage() {

        //carregar variáveis
        logRetorno = new StringBuffer();
        processando = false;
        indicator = new AjaxIndicatorAppender();
        pastaPath = "";
        pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
        pastaPathReport = pathAppToWebapp + File.separatorChar + "reports" + File.separatorChar;
        totalProcessado = new TotalProcessado();
        totalProgressBar = new HashMap<>();
        listaProjetos = new ArrayList<>();

        lbProjetosSize = new Label("lbProjetosSize", Model.of("0"));
        lbProjetosSize.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true);
        add(lbProjetosSize);

        criarTimer();

        criarListaProjetos();

        linkCSVFinal = new ExternalLink("linkCSVFinal", File.separatorChar + "reports" + File.separatorChar + dataProcessamentoAtual + File.separatorChar + "all_report_by_testsmells.csv");
        linkCSVFinal.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(linkCSVFinal);

        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        criarForm();

        criarBotaoProcessarTodos();

        lbPastaSelecionada = new Label("lbPastaSelecionada", pastaPath);
        add(lbPastaSelecionada);

        progressBar = new ProgressBar("progress", Model.of(0));
        add(this.progressBar);
    }

    private void criarBotaoProcessarTodos(){
        processarTodos = new IndicatingAjaxLink<String>("processarTodos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                processando = true;
                List<Projeto> listaParaProcessar = new ArrayList<>();
                for (Projeto projeto : listaProjetos) {
                    if (projeto.getParaProcessar()) {
                        listaParaProcessar.add(projeto);
                    }
                }
                JNoseCore.processarProjetos(listaParaProcessar, dataProcessamentoAtual, pastaPathReport, totalProcessado, logRetorno);
            }
        };
        processarTodos.setEnabled(false);
        add(processarTodos);
    }

    private void criarListaProjetos(){
        lvProjetos = new ListView<Projeto>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {
                Projeto projeto = item.getModelObject();

                AjaxCheckBox paraProcessarACB = new AjaxCheckBox("paraProcessarACB", new PropertyModel(projeto, "paraProcessar")) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {

                        List<Projeto> listaProjetosProcessar = new ArrayList<>();
                        for (Projeto projeto : listaProjetos)
                            if (projeto.getParaProcessar()) listaProjetosProcessar.add(projeto);

                        if (listaProjetosProcessar.size() > 0) {
                            processarTodos.setEnabled(true);
                        } else {
                            processarTodos.setEnabled(false);
                        }
                        target.add(processarTodos);

                        lbProjetosSize.setDefaultModel(Model.of(listaProjetosProcessar.size()));
                        target.add(lbProjetosSize);
                    }
                };
                item.add(paraProcessarACB);

                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("projeto", projeto.getPath()));

                WebMarkupContainer progressProject = new WebMarkupContainer("progressProject");
                progressProject.setOutputMarkupPlaceholderTag(true);
                progressProject.setOutputMarkupId(true);//style="width: 25%"
                progressProject.add(new AttributeModifier("style", "width: " + projeto.getProcentagem() + "%"));
                item.add(progressProject);
                projeto.progressProject = progressProject;

                Label lbPorcetagem = new Label("lbPorcentagem", projeto.getProcentagem());
                lbPorcetagem.setOutputMarkupId(true);
                lbPorcetagem.setOutputMarkupPlaceholderTag(true);
                projeto.lbPorcentagem = lbPorcetagem;
                progressProject.add(lbPorcetagem);
            }
        };
        lvProjetos.setOutputMarkupId(true);
        lvProjetos.setOutputMarkupPlaceholderTag(true);
        add(lvProjetos);
    }

    private void criarForm(){
        Form form = new Form<>("form");
        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        tfPastaPath.setRequired(true);
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                dataProcessamentoAtual = JNoseCore.dateNowFolder();
                totalProcessado.setValor(0);
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));

                File file = new File(pastaPath);
                listaProjetos = JNoseCore.listaProjetos(file.toURI());
                lvProjetos.setList(listaProjetos);

                processarTodos.setEnabled(true);
                lbProjetosSize.setDefaultModel(Model.of(listaProjetos.size()));

            }
        };
        form.add(btEnviar);
        add(form);
    }

    private void criarTimer(){
        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                progressBar.setModel(Model.of(totalProcessado.getValor()));
                target.add(progressBar);

                Boolean todosProjetosProcessados = true;

                List<Projeto> listaProjetosProcessar = new ArrayList<>();

                for (Projeto projeto : listaProjetos) {
                    if (projeto.getParaProcessar()) {
                        listaProjetosProcessar.add(projeto);
                    }
                }

                for (Projeto projeto : listaProjetosProcessar) {

                    Label lbPorcentagem = projeto.lbPorcentagem;
                    lbPorcentagem.setDefaultModel(Model.of(projeto.getProcentagem()));
                    target.add(lbPorcentagem);

                    WebMarkupContainer progressProject = projeto.progressProject;
                    progressProject.add(new AttributeModifier("style", "width: " + projeto.getProcentagem() + "%"));
                    target.add(progressProject);

                    todosProjetosProcessados = todosProjetosProcessados && projeto.getProcessado();
                }

                if (todosProjetosProcessados) {
                    totalProcessado.setValor( (100 - totalProcessado.getValor()) + totalProcessado.getValor());
                    processando = false;
                }

                boolean processado = true;
                for (Projeto p : listaProjetosProcessar) {
                    processado = processado && p.getProcessado();
                }

                if (dataProcessamentoAtual != null && !dataProcessamentoAtual.isEmpty()) {
                    linkCSVFinal.setDefaultModel(Model.of("/reports/" + dataProcessamentoAtual + File.separatorChar + "all_report_by_testsmells.csv"));
                    target.add(linkCSVFinal);
                }

            }
        };
        add(timer);
    }

}