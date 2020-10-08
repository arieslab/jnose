package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.ImprimirPage;
import br.ufba.jnose.pages.charts.TestSmellsBarOptions;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;
import org.apache.wicket.markup.html.basic.Label;

import java.util.List;

public class ChartsPage extends ImprimirPage {
    private static final long serialVersionUID = 1L;

    public ChartsPage(String title, TestSmellsBarOptions options) {
        add(new Label("title",title));
        add(new Chart("chart", options));
    }

}