package br.ufba.jnose.pages;

import br.ufba.jnose.testfiledetector.Main;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.wicket.markup.html.WebPage;

import static java.lang.System.out;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    private String pastaPath = "/home/tassio/Experimento/projetos/base_blame/";

    private Label lbPastaSelecionada;

    private ProgressBar progressBar;

    private List<String> listaProjetos;

    private AjaxIndicatorAppender indicator = new AjaxIndicatorAppender();

    private ListView<String> lvProjetos;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        listaProjetos = new ArrayList<>();

        Form form = new Form<>("form");

        lvProjetos = new ListView<String>("lvProjetos", listaProjetos) {
            @Override
            protected void populateItem(ListItem item) {
                String projetoPath = (String) item.getModel().getObject();
                item.add(new Label("projeto", item.getModel()));

                ProgressBar progressBar = new ProgressBar("progress", Model.of(0)) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void onValueChanged(IPartialPageRequestHandler handler) {
//                        info("value: " + this.getDefaultModelObjectAsString());
//                        handler.add(feedback);
                    }
                    @Override
                    protected void onComplete(AjaxRequestTarget target) {
//                        info("completed!");
//                        target.add(feedback);
                    }
                };
                progressBar.setOutputMarkupId(true);
                item.add(progressBar);

                ExternalLink lkTestFileDetector = new ExternalLink("lkTestFileDetector", "");
                lkTestFileDetector.setOutputMarkupId(true);
                lkTestFileDetector.setEnabled(false);
                item.add(lkTestFileDetector);

                AjaxLink lkProcessarProjeto = new AjaxLink<String>("lkProcessarProjeto") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        System.out.println(projetoPath);
                        String csvLink = processarProjeto(projetoPath);
                        System.out.println("FINALIZADO!!");
                        lkTestFileDetector.setEnabled(true);
                        lkTestFileDetector.setDefaultModel(Model.of(csvLink));
                        target.add(lkTestFileDetector);

                        progressBar.setModel(Model.of(30));
                        target.add(progressBar);

//                        setResponsePage(HomePage.this);
                    }
                };

                item.add(lkProcessarProjeto);
            }
        };
        add(lvProjetos);

        // FeedbackPanel //
        FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));

        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        form.add(tfPastaPath);

        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                System.out.println(pastaPath);
                lbPastaSelecionada.setDefaultModel(Model.of(pastaPath));
                progressBar.setModel(Model.of(50));
                List<String> lista = listaPastas(pastaPath);
                lvProjetos.setList(lista);
            }
        };
        form.add(btEnviar);

        lbPastaSelecionada = new Label("lbPastaSelecionada", pastaPath);
        add(lbPastaSelecionada);


        // ProgressBar //
        progressBar = new ProgressBar("progress", Model.of(36)) {
            private static final long serialVersionUID = 1L;
            @Override
            public void onValueChanged(IPartialPageRequestHandler handler) {
                info("value: " + this.getDefaultModelObjectAsString());
                handler.add(feedback);
            }
            @Override
            protected void onComplete(AjaxRequestTarget target) {
//                timer.stop(target); //wicket6

                info("completed!");
                target.add(feedback);
            }
        };

        add(this.progressBar);

        add(form);

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

    private String processarProjeto(String pathProjeto) {
        String nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf("/"), pathProjeto.length() - 1);
        String retorno = "";
        try {
            retorno = Main.start(pathProjeto, nameProjeto, "/home/tassio/Desenvolvimento/jnose/jnose/src/main/webapp/reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retorno;
    }
}
