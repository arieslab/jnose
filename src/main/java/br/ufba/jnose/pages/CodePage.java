package br.ufba.jnose.pages;

import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.dto.ProjetoDTO;
import br.ufba.jnose.pages.base.ImprimirPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodePage extends ImprimirPage {
    private static final long serialVersionUID = 1L;
    private static int cont = 0;

    public CodePage(ProjetoDTO projeto, String title, String pathFile, int inicio, int fim) {
        add(new Label("title", "Code Test Smell: " + title));

        Map<Integer,String> mapaBlame = GitCore.blame(projeto.getPath(),pathFile);

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

        cont = 1;
        ListView lvCodigo = new ListView<String>("lvCodigo", linhas) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String linha = item.getModelObject();
                item.add(new Label("numero",cont));
                item.add(new Label("blame", mapaBlame.get(cont)));

                WebMarkupContainer container = new WebMarkupContainer("container");

                if(cont >= inicio && cont <= fim){
                    item.add(new AttributeAppender("style", "background-color: #ffe6ff;"));
                }

                container.add(new Label("codigo", linha));
                item.add(container);

                cont++;
            }
        };
        cont = 1;
        add(lvCodigo);
    }
}

