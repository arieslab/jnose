package io.github.arieslab.pages;

import io.github.arieslab.base.CSVCore;
import io.github.arieslab.base.Util;
import io.github.arieslab.pages.base.ImprimirPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.io.File;
import java.util.List;

/**
 * Page that displays analysis results in a tabular view with CSV export.
 */
public class ResultPage extends ImprimirPage {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs the result page with the given data table.
     *
     * @param todasLinhas the result rows (first row = headers)
     * @param title the page title
     * @param csvFileName the base name for the CSV export file
     * @param fontColorized whether to color numeric values (green=0, red=nonzero)
     */
    public ResultPage(List<List<String>> todasLinhas, String title, String csvFileName, boolean fontColorized) {

        add(new Label("title",title + " - View Sample (100 of " + todasLinhas.size() + ")"));

        DownloadLink lkEsportCSV = new DownloadLink("lkEsportCSV", gerarCSV(todasLinhas,csvFileName));
        add(lkEsportCSV);

        if(todasLinhas.size() > 101){
            todasLinhas = todasLinhas.subList(0, 101);
        }
        
        ListView<List<String>> lista = new ListView<List<String>>("lista", todasLinhas) {
            @Override
            protected void populateItem(ListItem<List<String>> item) {
                List<String> linha = item.getModelObject();
                for(int i = 0; i <= 35; i++){
                    lb(i,linha,item, fontColorized);
                }
            }
        };

        add(lista);
    }

    /**
     * Generates a CSV file from the result data.
     *
     * @param todasLinhas the data rows
     * @param csvFileName the base file name
     * @return the generated CSV file
     */
    private File gerarCSV(List<List<String>> todasLinhas, String csvFileName){
        String pathFile = CSVCore.criarCSV(todasLinhas, io.github.arieslab.base.Util.dateNowFolder(),csvFileName);
        return new File(pathFile);
    }

    /**
     * Adds a cell label to the current row at the given column index, with optional color highlighting.
     *
     * @param numero the column index
     * @param linha the data row
     * @param item the list item
     * @param fontColorized whether to color the cell
     */
    private void lb(int numero, List<String> linha, ListItem<List<String>> item, Boolean fontColorized){
        if(linha.size() > numero) {

            Label lb = new Label("lb" + numero, linha.get(numero));
            if(Util.isInt(linha.get(numero))){
                Integer n = Integer.parseInt(linha.get(numero));
                if(fontColorized) {
                    if (n != 0) {
                        lb.add(new AttributeModifier("style", "color:red"));
                    } else {
                        lb.add(new AttributeModifier("style", "color:green"));
                    }
                }
            }

            item.add(lb);
        }else{
            item.add(new Label("lb" + numero).setVisible(false));
        }
    }
}
