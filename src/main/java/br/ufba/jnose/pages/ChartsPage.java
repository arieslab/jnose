package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.BasePage;
import br.ufba.jnose.pages.charts.BasicBarOptions;
import br.ufba.jnose.pages.charts.TestSmellsBarOptions;
import de.adesso.wickedcharts.highcharts.options.*;
import de.adesso.wickedcharts.highcharts.options.series.SimpleSeries;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;
import org.apache.wicket.markup.html.basic.Label;

import java.util.Arrays;
import java.util.List;

public class ChartsPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public ChartsPage(List<List<String>> todasLinhas, String title) {
        super("ResultPage");
        add(new Label("title",title));
        TestSmellsBarOptions options = new TestSmellsBarOptions(todasLinhas);
        add(new Chart("chart", options));
    }

}