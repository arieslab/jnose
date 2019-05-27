package br.ufba.jnose.pages;

import br.ufba.jnose.testfiledetector.Main;
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

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    private String pastaPath = "/home/tassio/Experimento/projetos/base_blame/";

    private Label lbPastaSelecionada;

    private ProgressBar progressBar;

    private List<String> listaProjetos;

    private AjaxIndicatorAppender indicator = new AjaxIndicatorAppender();

    private ListView<String> lvProjetos;

    private TextArea taLog;

    private Integer totalProcessado;

    private Map<Integer, Integer> totalProgressBar;

    private Boolean processando = false;

    private WebMarkupContainer loadImg;

    private AjaxLink processarTodos;

    private Label lbProjetosSize;

    private Label footTime;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        lbProjetosSize = new Label("lbProjetosSize",Model.of("0"));
        lbProjetosSize.setOutputMarkupPlaceholderTag(true);
        lbProjetosSize.setOutputMarkupId(true);
        add(lbProjetosSize);

        footTime = new Label("footTime");
        footTime.setOutputMarkupId(true);
        footTime.setOutputMarkupPlaceholderTag(true);
        add(footTime);

        totalProgressBar = new HashMap<>();

        totalProcessado = 0;

        taLog = new TextArea("taLog");
        taLog.setOutputMarkupId(true);
        add(taLog);
        AjaxLink lkTextArea = new AjaxLink<String>("lkProcessarProjeto") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try (FileInputStream inputStream = new FileInputStream("/home/tassio/Desenvolvimento/jnose/jnose/pom.xml")) {
                    String everything = IOUtils.toString(inputStream);
//                    taLog.setModel(Model.of(everything));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        add(lkTextArea);

        AbstractAjaxTimerBehavior timer = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            int cont = 0;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                footTime.setDefaultModel(Model.of(cont + ""));
                cont++;
                target.add(footTime);

                progressBar.setModel(Model.of(totalProcessado));
                target.add(progressBar);

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

        lvProjetos = new ListView<String>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem item) {
                item.add(new Label("idx", item.getIndex()));
                String projetoPath = (String) item.getModel().getObject();
                item.add(new Label("projeto", item.getModel()));
            }
        };
        lvProjetos.setOutputMarkupId(true);
        add(lvProjetos);

        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                totalProcessado = 0;
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                List<String> lista = listaPastas(pastaPath);
                lvProjetos.setList(lista);
                processarTodos.setEnabled(true);
                lbProjetosSize.setDefaultModel(Model.of(lista.size()));
            }
        };
        form.add(btEnviar);

        processarTodos = new AjaxLink<String>("processarTodos") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                List<String> lista = listaPastas(pastaPath);
                processarThread(lista, target);
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


    private String processarThread(List<String> lista, AjaxRequestTarget target) {
        totalProcessado = 0;

        Integer totalLista = lista.size();
        Integer valorSoma = 100 / totalLista;

        String retorno = "";
        Thread guruThread1 = new Thread("TODOS1") {
            @Override
            public void run() {
                processando = true;
                for (String projetoPath : lista) {
                    processarTODOS(projetoPath, valorSoma);
                }
                int resto = 100 - totalProcessado;
                totalProcessado = totalProcessado + resto;
                processando = false;
            }
        };
        guruThread1.start();
        return retorno;
    }


    private String processarTODOS(String projetoPath, float valorProcProject) {
        Float valorSoma = valorProcProject/3;
        String csvFile = processarTestFileDetector(projetoPath);
        totalProcessado = totalProcessado + valorSoma.intValue();
        String csvMapping = processarTestFileMapping(csvFile, projetoPath);
        totalProcessado = totalProcessado + valorSoma.intValue();
        String csvTestSmells = processarTestSmellDetector(csvMapping, projetoPath);
        totalProcessado = totalProcessado + valorSoma.intValue();
        return csvTestSmells;
    }


    private List<String> listaPastas(String path) {
        java.io.File[] directories = new java.io.File(path).listFiles(java.io.File::isDirectory);
        List<String> listaPastas = new ArrayList<String>();

        for (java.io.File dir : directories) {
            String pathPom = dir.getAbsolutePath() + "/pom.xml";
            if (new File(pathPom).exists()) {
                out.println("Processando: " + dir.getAbsolutePath());
                listaPastas.add(dir.getAbsolutePath());
            } else {
                out.println("Não é um projeto MAVEN: " + dir.getAbsolutePath());
            }
        }

        return listaPastas;
    }

    private String processarTestFileDetector(String pathProjeto) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/"), pathProjeto.length() - 1);
        String pathCSV = "";
        try {
            pathCSV = Main.start(pathProjeto, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSV;
    }

    private String processarTestFileMapping(String pathFileCSV, String pathProjeto) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/"), pathProjeto.length() - 1);
        String pathCSVMapping = "";
        try {
            pathCSVMapping = br.ufba.jnose.testfilemapping.Main.start(pathFileCSV, pathProjeto, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCSVMapping;
    }


    private String processarTestSmellDetector(String pathCSVMapping, String pathProjeto) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/"), pathProjeto.length() - 1);
        String csvTestSmells = "";
        try {
            csvTestSmells = br.ufba.jnose.testsmelldetector.Main.start(pathCSVMapping, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvTestSmells;
    }

}
