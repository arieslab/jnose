package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class ByClassTestResultPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public ByClassTestResultPage(List<List<String>> todasLinhas) {

        ListView<List<String>> lista = new ListView<List<String>>("lista",todasLinhas) {
            @Override
            protected void populateItem(ListItem<List<String>> item) {
                List<String> linha = item.getModelObject();
                item.add(new Label("lb0",linha.get(0)));
                item.add(new Label("lb1",linha.get(1)));
                item.add(new Label("lb2",linha.get(2)));

            }
        };

        add(lista);

    }
}

class TestSmellsDTO {
    public String colum1;
    public String colum2;
    public String colum3;
}