package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.util.JNoseUtils;
import br.ufba.jnose.core.cobertura.ReportGenerator;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.util.ResultsWriter;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.http.Cookie;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.time.Duration;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.out;

public class HomePage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String pastaPath = "";
    private String pathAppToWebapp = WebApplication.get().getServletContext().getRealPath("");
    private String pastaPathReport = pathAppToWebapp + File.separatorChar + "reports" + File.separatorChar;
    private Label lbPastaSelecionada;
    private ProgressBar progressBar;
    private List<Projeto> listaProjetos;
    private AjaxIndicatorAppender indicator = new AjaxIndicatorAppender();
    private ListView<Projeto> lvProjetos;
    private Label taLog;
    private Label taLogInfo;
    private Integer totalProcessado;
    private Map<Integer, Integer> totalProgressBar;
    private Boolean processando = false;
    private WebMarkupContainer loadImg;
    private IndicatingAjaxLink processarTodos;
    private Label lbProjetosSize;
    private String logRetorno = "";
    static public String logRetornoInfo = "";
    private String dataProcessamentoAtual;
    private boolean mesclado = false;
    private ExternalLink linkCSVFinal;
    private String newReport = "";
    private boolean processarCobertura;

    public HomePage() {

        Cookie pastaPathCookie = ((WebRequest) getRequest()).getCookie("pastaPath");
        if (pastaPathCookie != null) {
            pastaPath = pastaPathCookie.getValue();
        } else {
            pastaPath = "";
        }

        logRetornoInfo = "pastaPathCookie: " + pastaPath + " <br>" + logRetornoInfo;

        AjaxCheckBox acbCobertura = new AjaxCheckBox("acbCobertura", new PropertyModel(this, "processarCobertura")) {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                WicketApplication.COBERTURA_ON = processarCobertura;
                out.println("COBERTURA_ON: " + processarCobertura);
                logRetornoInfo = "COBERTURA_ON: " + processarCobertura + " <br>" + logRetornoInfo;
            }
        };
        add(acbCobertura);

        lbProjetosSize = new Label("lbProjetosSize", Model.of("0"));
        lbProjetosSize.setOutputMarkupPlaceholderTag(true);
        lbProjetosSize.setOutputMarkupId(true);
        add(lbProjetosSize);

        totalProgressBar = new HashMap<>();

        totalProcessado = 0;

        taLog = new Label("taLog");
        taLog.setEscapeModelStrings(false);
        taLog.setOutputMarkupId(true);
        taLog.setOutputMarkupPlaceholderTag(true);
        add(taLog);

        taLogInfo = new Label("taLogInfo");
        taLogInfo.setEscapeModelStrings(false);
        taLogInfo.setOutputMarkupId(true);
        taLogInfo.setOutputMarkupPlaceholderTag(true);
        add(taLogInfo);

        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                progressBar.setModel(Model.of(totalProcessado));
                target.add(progressBar);

                taLog.setDefaultModel(Model.of(logRetorno));
                target.add(taLog);

                taLogInfo.setDefaultModel(Model.of(logRetornoInfo));
                target.add(taLogInfo);

                Boolean todosProjetosProcessados = true;

                List<Projeto> listaProjetosProcessar = new ArrayList<>();

                for (Projeto projeto : listaProjetos) {
                    if (projeto.getParaProcessar()) {
                        listaProjetosProcessar.add(projeto);
                    }
                }

                for (Projeto projeto : listaProjetosProcessar) {

                    WebMarkupContainer iconProcessado = projeto.iconProcessado;
                    iconProcessado.setVisible(projeto.getProcessado());
                    WebMarkupContainer iconNaoProcessado = projeto.iconNaoProcessado;
                    iconNaoProcessado.setVisible(!projeto.getProcessado());
                    target.add(iconProcessado);
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
                    totalProcessado = (100 - totalProcessado) + totalProcessado;
                    processando = false;
                }

                boolean processado = true;
                for (Projeto p : listaProjetosProcessar) {
                    processado = processado && p.getProcessado();
                }

                if (processado && mesclado == false && !listaProjetosProcessar.isEmpty()) {
                    mesclarGeral(listaProjetosProcessar, pastaPathReport + dataProcessamentoAtual + File.separatorChar);
                    mesclado = true;
                }

                if (processando) {
                    if (!loadImg.isVisible()) {
                        loadImg.setVisible(true);
                        target.add(loadImg);
                    }
                } else {
                    if (loadImg.isVisible()) {
                        loadImg.setVisible(false);
                        target.add(loadImg);
                    }
                }

                if (dataProcessamentoAtual != null && !dataProcessamentoAtual.isEmpty()) {
                    linkCSVFinal.setDefaultModel(Model.of("/reports/" + dataProcessamentoAtual + File.separatorChar + "all_testsmesll.csv"));
                    target.add(linkCSVFinal);

                    out.println(newReport);
                }

            }
        };
        add(timer);

        loadImg = new WebMarkupContainer("loadImg");
        loadImg.setOutputMarkupId(true);
        loadImg.setVisible(false);
        loadImg.setOutputMarkupPlaceholderTag(true);
        add(loadImg);

        listaProjetos = new ArrayList<>();

        Form form = new Form<>("form");

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
                item.add(btMdel);

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

        linkCSVFinal = new ExternalLink("linkCSVFinal", File.separatorChar + "reports" + File.separatorChar + dataProcessamentoAtual + File.separatorChar + "all_testsmesll.csv");
        linkCSVFinal.setOutputMarkupId(true);
        linkCSVFinal.setOutputMarkupPlaceholderTag(true);
        add(linkCSVFinal);

        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        tfPastaPath.setRequired(true);
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                mesclado = false;
                dataProcessamentoAtual = dateNowFolder();
                logRetorno = "";
                logRetornoInfo = "";
                totalProcessado = 0;
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));

                File file = new File(pastaPath);
                listaProjetos = listaProjetos(file.toURI());
                lvProjetos.setList(listaProjetos);

                processarTodos.setEnabled(true);
                lbProjetosSize.setDefaultModel(Model.of(listaProjetos.size()));

                Cookie pastaPathCookie = new Cookie("pastaPath", "\""+pastaPath+"\"");
                ((WebResponse) getResponse()).addCookie(pastaPathCookie);
            }
        };
        form.add(btEnviar);

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
                processarProjetos(listaParaProcessar, dataProcessamentoAtual);
            }
        };
        processarTodos.setEnabled(false);
        add(processarTodos);

        lbPastaSelecionada = new Label("lbPastaSelecionada", pastaPath);
        add(lbPastaSelecionada);

        progressBar = new ProgressBar("progress", Model.of(0));
        add(this.progressBar);
        add(form);
    }


    private void processarProjetos(List<Projeto> lista, String folderTime) {

        boolean success = (new File(pastaPathReport + folderTime + File.separatorChar)).mkdirs();
        if (!success) System.out.println("Pasta Criada...");

        totalProcessado = 0;

        Integer totalLista = lista.size();
        Integer valorSoma;
        if (totalLista > 0) {
            valorSoma = 100 / totalLista;
        } else {
            valorSoma = 0;
        }

        for (Projeto projeto : lista) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        processarProjeto(projeto, valorSoma, folderTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        projeto.bugs = projeto.bugs + "\n" + e.getMessage();
                    }
                }
            }.start();
        }

    }


    private String processarProjeto(Projeto projeto, float valorProcProject, String folderTime) throws IOException {
        logRetorno = dateNow() + projeto.getName() + " - started <br>" + logRetorno;
        Float valorSoma = valorProcProject / 4;

        totalProcessado = 5;
        projeto.setProcentagem(totalProcessado);

        if (WicketApplication.COBERTURA_ON) {
            processarCobertura(projeto, folderTime);
        }


        projeto.setProcessado2(true);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(25);

        String csvFile = processarTestFileDetector(projeto.getPath(), folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(50);

        List<TestClass> listaTestClass = JNoseUtils.getFilesTest(projeto.getPath());
        String csvMapping = processarTestFileMapping(listaTestClass,csvFile, projeto.getPath(), folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(75);

        newReport = JNoseUtils.newReport(listaTestClass, pastaPathReport + File.separator + folderTime);
        out.println(newReport);

        String csvTestSmells = processarTestSmellDetector(csvMapping, projeto.getPath(), folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(100);

        projeto.setProcessado(true);
        return csvTestSmells;
    }

    private void processarCobertura(Projeto projeto, String folderTime) {
        logRetorno = dateNow() + projeto.getName() + " - <font style='color:blue'>Cobertura</font> <br>" + logRetorno;
        try {
            execCommand("mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Drat.skip=true", projeto.getPath());
            ReportGenerator reportGenerator = new ReportGenerator(new File(projeto.getPath()), new File(pastaPathReport + folderTime + File.separatorChar));
            reportGenerator.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Projeto> listaProjetos(URI path) {
        java.io.File[] directories = new File(path).listFiles(java.io.File::isDirectory);
        List<Projeto> lista = new ArrayList<Projeto>();

        if (directories != null) {
            for (java.io.File dir : directories) {
                String pathPom = dir.getAbsolutePath() + File.separatorChar + "pom.xml";

                if (new File(pathPom).exists()) {
                    String pathProjeto = dir.getAbsolutePath().trim();
                    String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
                    lista.add(new Projeto(nameProjeto, pathProjeto));
                } else {
                    String msg = "Não é um projeto MAVEN: " + dir.getAbsolutePath();
                    out.println(msg);
                    logRetornoInfo = msg + " <br>" + logRetornoInfo;
                }
            }
        }

        return lista;
    }

    private String processarTestFileDetector(String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:red'>TestFileDetector</font> <br>" + logRetorno;
        String pathCSV = "";
        try {
            pathCSV = JNoseUtils.testfiledetector(pathProjeto, nameProjeto, pastaPathReport + folderTime + File.separatorChar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSV;
    }

    private String processarTestFileMapping(List<TestClass> listTestClass,String pathFileCSV, String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:green'>TestFileMapping</font> <br>" + logRetorno;
        String pathCSVMapping = "";
        try {
            pathCSVMapping = JNoseUtils.testfilemapping(listTestClass,pathFileCSV, pathProjeto, nameProjeto, pastaPathReport + folderTime + File.separatorChar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSVMapping;
    }


    private String processarTestSmellDetector(String pathCSVMapping, String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector</font> <br>" + logRetorno;
        String csvTestSmells = "";
        try {
            csvTestSmells = br.ufba.jnose.core.testsmelldetector.Main.start(pathCSVMapping, nameProjeto, pastaPathReport + folderTime + File.separatorChar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvTestSmells;
    }

    private String dateNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")) + " - ";
    }

    private String dateNowFolder() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private static void execCommand(final String commandLine, String pathExecute) {
        int r = 0;
        try {
            Process p = Runtime.getRuntime().exec(commandLine, null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                System.out.println(lineOut);
                logRetornoInfo = lineOut + " <br>" + logRetornoInfo;
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> columnNames;

    private static void gerarDadosGeral() {
        List<String> columnValues;
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        columnNames = testSmellDetector.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestFileName");
        columnNames.add(2, "ProductionFileName");
        columnNames.add("LOC");
        //jacoco
        if (WicketApplication.COBERTURA_ON) {
            columnNames.add("INSTRUCTION_MISSED");
            columnNames.add("INSTRUCTION_COVERED");
            columnNames.add("BRANCH_MISSED");
            columnNames.add("BRANCH_COVERED");
            columnNames.add("LINE_MISSED");
            columnNames.add("LINE_COVERED");
            columnNames.add("COMPLEXITY_MISSED");
            columnNames.add("COMPLEXITY_COVERED");
            columnNames.add("METHOD_MISSED");
            columnNames.add("METHOD_COVERED");
        }
    }


    private void mesclarGeral(List<Projeto> listaProjetos, String reportPath) {

        logRetorno = dateNow() + "<font style='color:orange'>Mesclando resultados</font> <br>" + logRetorno;

        try {
            ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(reportPath + "all" + "_testsmesll.csv");
            resultsWriter.writeColumnName(br.ufba.jnose.core.testsmelldetector.Main.columnNames);

            if (listaProjetos.size() != 0) {
                for (Projeto projeto : listaProjetos) {

                    File jacocoFile = new File(reportPath + projeto.getName() + "_testsmesll.csv");
                    FileReader jacocoFileReader = new FileReader(jacocoFile);
                    BufferedReader jacocoIn = new BufferedReader(jacocoFileReader);

                    boolean pularLinha = false;
                    String str;
                    while ((str = jacocoIn.readLine()) != null) {
                        if (pularLinha) {
                            resultsWriter.writeLine(newArrayList(str.split(",")));
                        } else {
                            pularLinha = true;
                        }
                    }
                    jacocoIn.close();
                    jacocoFileReader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}