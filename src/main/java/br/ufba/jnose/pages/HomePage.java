package br.ufba.jnose.pages;

import br.ufba.jnose.cobertura.ReportGenerator;
import br.ufba.jnose.testfiledetector.Main;
import br.ufba.jnose.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.util.ResultsWriter;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.thread.ICode;
import org.apache.wicket.util.thread.Task;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.TimeFrame;
import org.slf4j.Logger;

import static java.lang.System.out;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static java.lang.System.out;
import static com.google.common.collect.Lists.newArrayList;


public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    //    private String pastaPath = "/home/tassio/Experimento/projetos/base_artigo_2/";
    private String pastaPath = "/home/tassio/Experimento/projetos/base_blame/";

    private Label lbPastaSelecionada;

    private ProgressBar progressBar;

    private List<Projeto> listaProjetos;

    private AjaxIndicatorAppender indicator = new AjaxIndicatorAppender();

    private ListView<Projeto> lvProjetos;

    private Label taLog;

    private Integer totalProcessado;

    private Map<Integer, Integer> totalProgressBar;

    private Boolean processando = false;

    private WebMarkupContainer loadImg;

    private AjaxLink processarTodos;

    private Label lbProjetosSize;

    private Label footTime;

    private String logRetorno = "";

    private String dataProcessamentoAtual;

    private boolean mesclado = false;

    private ExternalLink linkCSVFinal;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        lbProjetosSize = new Label("lbProjetosSize", Model.of("0"));
        lbProjetosSize.setOutputMarkupPlaceholderTag(true);
        lbProjetosSize.setOutputMarkupId(true);
        add(lbProjetosSize);

        footTime = new Label("footTime");
        footTime.setOutputMarkupId(true);
        footTime.setOutputMarkupPlaceholderTag(true);
        add(footTime);

        totalProgressBar = new HashMap<>();

        totalProcessado = 0;

        taLog = new Label("taLog");
        taLog.setEscapeModelStrings(false);
        taLog.setOutputMarkupId(true);
        taLog.setOutputMarkupPlaceholderTag(true);
        add(taLog);

        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                footTime.setDefaultModel(Model.of(cont + ""));
                cont++;
                target.add(footTime);

                progressBar.setModel(Model.of(totalProcessado));
                target.add(progressBar);

                taLog.setDefaultModel(Model.of(logRetorno));
                target.add(taLog);

                Boolean todosProjetosProcessados = true;

                for (Projeto projeto : listaProjetos) {
//                    Label lbProcessado = projeto.lbProcessado;
//                    lbProcessado.setDefaultModel(Model.of(projeto.getProcessado()));
//                    target.add(lbProcessado);

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
                for (Projeto p : listaProjetos) {
                    processado = processado && p.getProcessado();
                }

                if(processado && mesclado == false && !listaProjetos.isEmpty()) {
                    mesclarGeral(listaProjetos, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + dataProcessamentoAtual + "/");
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

                if(dataProcessamentoAtual != null && !dataProcessamentoAtual.isEmpty()) {
                    linkCSVFinal.setDefaultModel(Model.of("/reports/" + dataProcessamentoAtual + "/" + "all_testsmesll.csv"));
                    target.add(linkCSVFinal);
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

                ExternalLink linkCSV0 = new ExternalLink("linl0", "/reports/" + dataProcessamentoAtual + "/" + projeto.getName() + "_jacoco.csv");
                ExternalLink linkCSV1 = new ExternalLink("linl1", "/reports/" + dataProcessamentoAtual + "/" + projeto.getName() + "_testfiledetection.csv");
                ExternalLink linkCSV2 = new ExternalLink("linl2", "/reports/" + dataProcessamentoAtual + "/" + projeto.getName() + "_testmappingdetector.csv");
                ExternalLink linkCSV3 = new ExternalLink("linl3", "/reports/" + dataProcessamentoAtual + "/" + projeto.getName() + "_testsmesll.csv");

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

        linkCSVFinal = new ExternalLink("linkCSVFinal", "/reports/" + dataProcessamentoAtual + "/" + "all_testsmesll.csv");
        linkCSVFinal.setOutputMarkupId(true);
        linkCSVFinal.setOutputMarkupPlaceholderTag(true);
        add(linkCSVFinal);

        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                mesclado = false;
                dataProcessamentoAtual = dateNowFolder();
                logRetorno = "";
                totalProcessado = 0;
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));

                listaProjetos = listaProjetos(pastaPath);
                lvProjetos.setList(listaProjetos);

                processarTodos.setEnabled(true);
                lbProjetosSize.setDefaultModel(Model.of(listaProjetos.size()));
            }
        };
        form.add(btEnviar);

        processarTodos = new AjaxLink<String>("processarTodos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                processando = true;
                processarProjetos(listaProjetos, dataProcessamentoAtual);
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

        boolean success = (new File("/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + folderTime + "/")).mkdirs();
        if (!success) System.out.println("Pasta Criada...");

        totalProcessado = 0;

        Integer totalLista = lista.size();
        Integer valorSoma = 100 / totalLista;

        for (Projeto projeto : lista) {
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        processarProjeto2(projeto, valorSoma, folderTime);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        projeto.bugs = projeto.bugs + "\n" + e.getMessage();
//                    }
//                }
//            }.start();
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

    private void processarProjeto2(Projeto projeto, float valorProcProject, String folderTime) {
        Float valorSoma = valorProcProject / 4;

        processarCobertura(projeto, folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(25);
        projeto.setProcessado2(true);
    }


    private String processarProjeto(Projeto projeto, float valorProcProject, String folderTime) {
        logRetorno = dateNow() + projeto.getName() + " - started <br>" + logRetorno;
        Float valorSoma = valorProcProject / 4;

        totalProcessado = 5;
        projeto.setProcentagem(totalProcessado);

        processarCobertura(projeto, folderTime);
        projeto.setProcessado2(true);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(25);

        String csvFile = processarTestFileDetector(projeto.getPath(), folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(50);

        String csvMapping = processarTestFileMapping(csvFile, projeto.getPath(), folderTime);
        totalProcessado = totalProcessado + valorSoma.intValue();
        projeto.setProcentagem(75);

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
            ReportGenerator reportGenerator = new ReportGenerator(new File(projeto.getPath()), new File("/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + folderTime + "/"));
            reportGenerator.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Projeto> listaProjetos(String path) {
        java.io.File[] directories = new java.io.File(path).listFiles(java.io.File::isDirectory);
        List<Projeto> lista = new ArrayList<Projeto>();

        if(directories != null){
            for (java.io.File dir : directories) {
                String pathPom = dir.getAbsolutePath() + "/pom.xml";
                if (new File(pathPom).exists()) {
                    String pathProjeto = dir.getAbsolutePath().trim();
                    String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/") + 1, pathProjeto.length());
                    lista.add(new Projeto(nameProjeto, pathProjeto));
                } else {
                    out.println("Não é um projeto MAVEN: " + dir.getAbsolutePath());
                }
            }
        }

        return lista;
    }

    private String processarTestFileDetector(String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/") + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:red'>TestFileDetector</font> <br>" + logRetorno;
        String pathCSV = "";
        try {
            pathCSV = Main.start(pathProjeto, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + folderTime + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSV;
    }

    private String processarTestFileMapping(String pathFileCSV, String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/") + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:green'>TestFileMapping</font> <br>" + logRetorno;
        String pathCSVMapping = "";
        try {
            pathCSVMapping = br.ufba.jnose.testfilemapping.Main.start(pathFileCSV, pathProjeto, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + folderTime + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSVMapping;
    }


    private String processarTestSmellDetector(String pathCSVMapping, String pathProjeto, String folderTime) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/") + 1, pathProjeto.length());
        logRetorno = dateNow() + nameProjeto + " - <font style='color:yellow'>TestSmellDetector</font> <br>" + logRetorno;
        String csvTestSmells = "";
        try {
            csvTestSmells = br.ufba.jnose.testsmelldetector.Main.start(pathCSVMapping, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports/" + folderTime + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvTestSmells;
    }


//    private String processarCobertura(String pathCSVMapping, String pathProjeto, String folderTime){
//        try{
//            br.ufba.jnose.cobertura.Main.start();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "";
//    }

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
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> columnNames;

    private static void gerarDadosGeral() {
//        String csvTestSmells = reportPath+projectName+"_testsmesll.csv";

//        ResultsWriter resultsWriter = null;
//        try {
//            resultsWriter = ResultsWriter.createResultsWriter(csvTestSmells);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        List<String> columnValues;
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();
        columnNames = testSmellDetector.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestFileName");
        columnNames.add(2, "ProductionFileName");
        columnNames.add("LOC");
        //jacoco
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
//        try {
//            resultsWriter.writeColumnName(columnNames);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private void mesclarGeral(List<Projeto> listaProjetos, String reportPath) {

        logRetorno = dateNow() + "<font style='color:orange'>Mesclando resultados</font> <br>" + logRetorno;


        try {
            ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(reportPath + "all" + "_testsmesll.csv");
            resultsWriter.writeColumnName(br.ufba.jnose.testsmelldetector.Main.columnNames);

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
//                    jacocoFile.deleteOnExit();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
