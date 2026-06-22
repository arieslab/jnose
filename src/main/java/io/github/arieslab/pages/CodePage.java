package io.github.arieslab.pages;

import io.github.arieslab.base.GitCore;
import io.github.arieslab.dtolocal.ProjetoDTO;
import io.github.arieslab.pages.base.ImprimirPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import static io.github.arieslab.base.Util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page that displays source code with blame annotations and highlights the test smell line range.
 */
public class CodePage extends ImprimirPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(CodePage.class.getName());

    private static int cont = 0;

    /**
     * Constructs a code view page showing the source file with blame and smell range highlighting.
     *
     * @param projeto the project DTO
     * @param title the smell name
     * @param pathFile the source file path
     * @param range the line range descriptor (e.g. "1-5", "1,3,5", or single number)
     */
    public CodePage(ProjetoDTO projeto, String title, String pathFile, String range) {
        add(new Label("title", "Code Test Smell: " + title + " ["+range+"]"));

        Map<Integer,String> mapaBlame = GitCore.blame(projeto.getPath(),pathFile);

        List<String> linhas = new ArrayList();

        try {
            File file = new File(pathFile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                linhas.add(line);
            }
            fr.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read file: " + pathFile, e);
        }

        cont = 1;
        ListView lvCodigo = new ListView<String>("lvCodigo", linhas) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String linha = item.getModelObject();
                item.add(new Label("numero",cont));
                item.add(new Label("blame", mapaBlame.get(cont)));

                WebMarkupContainer container = new WebMarkupContainer("container");

                if(range.contains("-")){
                    String[] ranger2 = range.split("-");
                    int inicio = Integer.parseInt(ranger2[0].trim());
                    int fim = Integer.parseInt(ranger2[1].trim());
                    if(cont >= inicio && cont <= fim){
                        item.add(new AttributeAppender("style", "background-color: #ffe6ff;"));
                    }
                }else if(range.contains(",")){
                    String[] ranger2 = range.replace(" ","").split(",");
                    List<String> lista = Arrays.asList(ranger2);
                    if(lista.contains(cont+"")){
                        item.add(new AttributeAppender("style", "background-color: #ffe6ff;"));
                    }
                }else if(isInt(range.trim())){
                    int range2 = Integer.parseInt(range.trim());
                    if(range2 == cont){
                        item.add(new AttributeAppender("style", "background-color: #ffe6ff;"));
                    }
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
