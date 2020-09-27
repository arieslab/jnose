package br.ufba.jnose.pages;

import br.ufba.jnose.core.CSVCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.io.File;
import java.util.List;

public class ResultPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public ResultPage(List<List<String>> todasLinhas, String title, String csvFileName) {

        add(new Label("title",title));

        DownloadLink lkEsportCSV = new DownloadLink("lkEsportCSV",gerarCSV(todasLinhas,csvFileName));
        add(lkEsportCSV);

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

    private File gerarCSV(List<List<String>> todasLinhas, String csvFileName){
        String pathFile = CSVCore.criarCSV(todasLinhas, JNoseCore.dateNowFolder(),csvFileName);
        return new File(pathFile);
    }

    private void lb(int numero, List<String> linha, ListItem<List<String>> item){
        if(linha.size() > numero) {
            item.add(new Label("lb" + numero, linha.get(numero)));
        }else{
            item.add(new Label("lb" + numero).setVisible(false));
        }
    }
}