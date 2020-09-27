package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class ResultPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public ResultPage(List<List<String>> todasLinhas, String title) {

        add(new Label("title",title));

        ListView<List<String>> lista = new ListView<List<String>>("lista",todasLinhas) {
            @Override
            protected void populateItem(ListItem<List<String>> item) {
                List<String> linha = item.getModelObject();
                for(int i = 0; i <= 26; i++){
                    lb(i,linha,item);
                }
            }
        };

        add(lista);
    }

    private void lb(int numero, List<String> linha, ListItem<List<String>> item){
        if(linha.size() > numero) {
            item.add(new Label("lb" + numero, linha.get(numero)));
        }else{
            item.add(new Label("lb" + numero).setVisible(false));
        }
    }
}