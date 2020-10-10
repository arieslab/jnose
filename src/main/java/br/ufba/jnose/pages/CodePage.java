package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.ImprimirPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CodePage extends ImprimirPage {
    private static final long serialVersionUID = 1L;
    private static int cont = 0;

    public CodePage(String title, String pathFile, int inicio, int fim) {
        add(new Label("title", "Code Test Smell: " + title));

        List<String> linhas = new ArrayList();

        try {
            File file = new File(pathFile);    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            String line;
            while ((line = br.readLine()) != null) {
                linhas.add(line);
            }
            fr.close();    //closes the stream and release the resources
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int[] contLinha = {1};
        ListView lvCodigo = new ListView<String>("lvCodigo", linhas) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String linha = item.getModelObject();
                item.add(new Label("numero",contLinha[0]));

                WebMarkupContainer container = new WebMarkupContainer("container");

                if(contLinha[0] >= inicio && contLinha[0] <= fim){
                    container.add(new AttributeAppender("style", "background-color: #ffe6ff;"));
                }

                container.add(new Label("codigo", linha));
                item.add(container);

                contLinha[0]++;
            }
        };
        add(lvCodigo);
    }
}

