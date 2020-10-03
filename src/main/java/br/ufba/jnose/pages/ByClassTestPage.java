package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.dto.TotalProcessado;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.time.Duration;
import java.io.*;
import java.util.*;
import java.util.List;

import static java.lang.System.out;

public class ByClassTestPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String pastaPath;
    private String pathAppToWebapp;
    private String pastaPathReport;
//    private Label lbPastaSelecionada;
    private ProgressBar progressBar;
    private List<Projeto> listaProjetos;
    private AjaxIndicatorAppender indicator;
    private ListView<Projeto> lvProjetos;
    private Label taLog;
//    private Label taLogInfo;
    private TotalProcessado totalProcessado;
    private Map<Integer, Integer> totalProgressBar;
    private Boolean processando;
    private WebMarkupContainer loadImg;
    private IndicatingAjaxLink processarTodos;
//    private Label lbProjetosSize;
    private StringBuffer logRetorno;
//    private StringBuffer logRetornoInfo;
    private String dataProcessamentoAtual;
    private boolean mesclado;
    private ExternalLink linkCSVFinal;
    private boolean processarCobertura;
    private List<List<String>> listaResultado;

    public ByClassTestPage() {
        super("ByClassTestPage");

        //Carregando variáveis
        indicator = new AjaxIndicatorAppender();
        pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
        pastaPathReport = pathAppToWebapp + File.separatorChar + "reports" + File.separatorChar;
        pastaPath = "";
        mesclado = false;
//        logRetornoInfo = new StringBuffer();
        logRetorno = new StringBuffer();
        processando = false;
        processarCobertura = false;
        totalProcessado = new TotalProcessado();
//        logRetornoInfo.append("pastaPath: " + pastaPath + " <br>");
        totalProgressBar = new HashMap<>();
        totalProcessado.setValor(0);

        criarCheckBoxCobertura();

//        lbProjetosSize = new Label("lbProjetosSize", Model.of("0"));
//        lbProjetosSize.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true);
//        add(lbProjetosSize);

        taLog = new Label("taLog");
        taLog.setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(taLog);

//        taLogInfo = new Label("taLogInfo");
//        taLogInfo.setEscapeModelStrings(false).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
//        add(taLogInfo);

        criarTimer();

        loadImg = new WebMarkupContainer("loadImg");
        loadImg.setOutputMarkupId(true).setVisible(false).setOutputMarkupPlaceholderTag(true);
        add(loadImg);

        criarListaProjetos();

        linkCSVFinal = new ExternalLink("linkCSVFinal", File.separatorChar + "reports" + File.separatorChar + dataProcessamentoAtual + File.separatorChar + "all_testsmesll.csv");
        linkCSVFinal.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(linkCSVFinal);

        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        criarForm();

        criarBotaoProcessarTodos();

//        lbPastaSelecionada = new Label("lbPastaSelecionada", pastaPath);
//        add(lbPastaSelecionada);

        progressBar = new ProgressBar("progress", Model.of(0));
        add(this.progressBar);

        Link lkResultado = new Link<String>("lkResultado") {
            @Override
            public void onClick() {
                setResponsePage(new ResultPage(listaResultado,"Result By ClassTest", "result_byclasstest_testsmells", true));
            }
        };
        add(lkResultado.setVisible(true));

        Link lkCharts = new Link<String>("lkCharts") {
            @Override
            public void onClick() {
                setResponsePage(new ChartsPage(listaResultado,"Charts By ClassTest"));
            }
        };
        add(lkCharts.setVisible(false));

        loadProjetos();
    }

    private void criarBotaoProcessarTodos(){
        processarTodos = new IndicatingAjaxLink<String>("processarTodos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
//                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                processando = true;
                List<Projeto> listaParaProcessar = new ArrayList<>();
                for (Projeto projeto : listaProjetos) {
                    if (projeto.getParaProcessar()) {
                        listaParaProcessar.add(projeto);
                    }
                }
                listaResultado = JNoseCore.processarProjetos2(listaParaProcessar, dataProcessamentoAtual, totalProcessado, pastaPathReport, logRetorno);
            }
        };
        processarTodos.setEnabled(false);
        add(processarTodos);
    }

    private void criarCheckBoxCobertura(){
        AjaxCheckBox acbCobertura = new AjaxCheckBox("acbCobertura", new PropertyModel(this, "processarCobertura")) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                WicketApplication.COBERTURA_ON = processarCobertura;
                out.println("COVERAGE_ON: " + processarCobertura);
                logRetorno.append("COVERAGE_ON: " + processarCobertura + " <br>");
            }
        };
        add(acbCobertura);
    }

    private void criarForm(){
//        Form form = new Form<>("form");
//
//        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
//        tfPastaPath.setRequired(true);
//        form.add(tfPastaPath);
//
//        Button btEnviar = new Button("btEnviar") {
//            @Override
//            public void onSubmit() {
//
//
//            }
//        };
//        form.add(btEnviar);
//        add(form);
    }

    private void loadProjetos(){
        mesclado = false;
        dataProcessamentoAtual = JNoseCore.dateNowFolder();
        logRetorno = new StringBuffer();
//        logRetornoInfo = new StringBuffer();
        totalProcessado.setValor(0);
//        lbPastaSelecionada.setDefaultModel(Model.of("./projects"));

        File file = new File("./projects");
        listaProjetos = JNoseCore.listaProjetos(file.toURI(),logRetorno);
        lvProjetos.setList(listaProjetos);

        processarTodos.setEnabled(true);
//        lbProjetosSize.setDefaultModel(Model.of(listaProjetos.size()));
    }

    private void criarListaProjetos(){
        listaProjetos = new ArrayList<>();

        lvProjetos = new ListView<Projeto>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {
                Projeto projeto = item.getModelObject();

                Link lkCharts = new Link<String>("lkCharts") {
                    @Override
                    public void onClick() {
                        setResponsePage(new ChartsPage(projeto.getResultado(),"Charts By ClassTest: " + projeto.getName()));
                    }
                };
                lkCharts.setEnabled(projeto.getProcessado());
                lkCharts.setOutputMarkupId(true);
                lkCharts.setOutputMarkupPlaceholderTag(true);
                item.add(lkCharts);
                projeto.lkCharts = lkCharts;

                Link lkResultado = new Link<String>("lkResultado") {
                    @Override
                    public void onClick() {
                        setResponsePage(new ResultPage(projeto.getResultado(),"Result By ClassTest: " + projeto.getName(), projeto.getName()+"_result_byclasstest_testsmells",true));
                    }
                };
                lkResultado.setEnabled(projeto.getProcessado());
                lkResultado.setOutputMarkupId(true);
                lkResultado.setOutputMarkupPlaceholderTag(true);
                item.add(lkResultado);
                projeto.lkResultado = lkResultado;

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

//                        lbProjetosSize.setDefaultModel(Model.of(listaProjetosProcessar.size()));
//                        target.add(lbProjetosSize);

                    }
                };
                item.add(paraProcessarACB);

                item.add(new Label("nomeProjeto", projeto.getName()));
                item.add(new Label("projeto", projeto.getPath()));

                WebMarkupContainer iconProcessado = new WebMarkupContainer("iconProcessado");
                iconProcessado.setVisible(projeto.getProcessado());
                iconProcessado.setOutputMarkupId(true);
                iconProcessado.setOutputMarkupPlaceholderTag(true);
                item.add(iconProcessado);
                projeto.iconProcessado = iconProcessado;

                WebMarkupContainer iconNaoProcessado = new WebMarkupContainer("iconNaoProcessado");
                iconNaoProcessado.setVisible(!projeto.getProcessado());
                iconNaoProcessado.setOutputMarkupId(true);
                iconNaoProcessado.setOutputMarkupPlaceholderTag(true);
                item.add(iconNaoProcessado);
                projeto.iconNaoProcessado = iconNaoProcessado;

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

                WebMarkupContainer btMdel = new WebMarkupContainer("btModel");
                btMdel.add(new AttributeModifier("data-target", "#modal" + projeto.getName()));
                item.add(btMdel.setVisible(false));

                WebMarkupContainer model = new WebMarkupContainer("model");
                model.add(new AttributeModifier("id", "modal" + projeto.getName()));
                item.add(model);

                ExternalLink linkCSV0 = new ExternalLink("linl0", File.separator + "reports" + File.separator + dataProcessamentoAtual + File.separatorChar + projeto.getName() + "_jacoco.csv");
                ExternalLink linkCSV1 = new ExternalLink("linl1", File.separator + "reports" + File.separator + dataProcessamentoAtual + File.separatorChar + projeto.getName() + "_testfiledetection.csv");
                ExternalLink linkCSV2 = new ExternalLink("linl2", File.separator + "reports" + File.separator + dataProcessamentoAtual + File.separatorChar + projeto.getName() + "_testmappingdetector.csv");
                ExternalLink linkCSV3 = new ExternalLink("linl3", File.separator + "reports" + File.separator + dataProcessamentoAtual + File.separatorChar + projeto.getName() + "_testsmesll.csv");

                model.add(linkCSV0);
                model.add(linkCSV1);
                model.add(linkCSV2);
                model.add(linkCSV3);

                model.add(new Label("nomeProjeto", projeto.getName()));

            }
        };
        lvProjetos.setOutputMarkupId(true);
        lvProjetos.setOutputMarkupPlaceholderTag(true);
        add(lvProjetos);
    }

    private void criarTimer(){
        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                progressBar.setModel(Model.of(totalProcessado.getValor()));
                target.add(progressBar);

                taLog.setDefaultModel(Model.of(logRetorno));
                target.add(taLog);

//                taLogInfo.setDefaultModel(Model.of(logRetornoInfo));
//                target.add(taLogInfo);

                Boolean todosProjetosProcessados = true;

                List<Projeto> listaProjetosProcessar = new ArrayList<>();

                for (Projeto projeto : listaProjetos) {
                    if (projeto.getParaProcessar()) {
                        listaProjetosProcessar.add(projeto);
                    }
                }

                for (Projeto projeto : listaProjetosProcessar) {

                    WebMarkupContainer lkResultado = projeto.lkResultado;
                    lkResultado.setEnabled(projeto.getProcessado());
                    target.add(lkResultado);

                    WebMarkupContainer lkCharts = projeto.lkCharts;
                    lkCharts.setEnabled(projeto.getProcessado());
                    target.add(lkCharts);

                    WebMarkupContainer iconProcessado = projeto.iconProcessado;
                    iconProcessado.setVisible(projeto.getProcessado());
                    target.add(iconProcessado);

                    WebMarkupContainer iconNaoProcessado = projeto.iconNaoProcessado;
                    iconNaoProcessado.setVisible(!projeto.getProcessado());
                    target.add(iconNaoProcessado);

                    Label lbPorcentagem = projeto.lbPorcentagem;
                    lbPorcentagem.setDefaultModel(Model.of(projeto.getProcentagem()));
                    target.add(lbPorcentagem);

                    WebMarkupContainer progressProject = projeto.progressProject;
                    progressProject.add(new AttributeModifier("style", "width: " + projeto.getProcentagem() + "%"));
                    target.add(progressProject);

                    todosProjetosProcessados = todosProjetosProcessados && projeto.getProcessado();
                }

                if (todosProjetosProcessados) {
                    totalProcessado.setValor((100 - totalProcessado.getValor()) + totalProcessado.getValor());
                    processando = false;
                }

                boolean processado = true;
                for (Projeto p : listaProjetosProcessar) {
                    processado = processado && p.getProcessado();
                }

//                if (processado && mesclado == false && !listaProjetosProcessar.isEmpty()) {
//                    JNoseCore.mesclarGeral(listaProjetosProcessar, pastaPathReport + dataProcessamentoAtual + File.separatorChar, logRetorno,dataProcessamentoAtual);
//                    mesclado = true;
//                }

                if(processando){
                    if (!loadImg.isVisible()) {
                        loadImg.setVisible(true);
                        target.add(loadImg);
                    }
                }else{
                    if (loadImg.isVisible()) {
                        loadImg.setVisible(false);
                        target.add(loadImg);
                    }
                }

                if (dataProcessamentoAtual != null && !dataProcessamentoAtual.isEmpty()) {
                    linkCSVFinal.setDefaultModel(Model.of("/reports/" + dataProcessamentoAtual + File.separatorChar + "all_testsmesll.csv"));
                    target.add(linkCSVFinal);
                }

            }
        };
        add(timer);
    }

}