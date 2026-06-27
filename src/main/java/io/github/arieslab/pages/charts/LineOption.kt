//package io.github.arieslab.pages.charts
//
//import de.adesso.wickedcharts.highcharts.options.*
//import de.adesso.wickedcharts.highcharts.options.series.SimpleSeries
//import java.util.Arrays
//
//class LineOption : Options() {
//    init {
//        chartOptions = ChartOptions().setType(SeriesType.LINE)
//        title = Title("My very own chart.")
//        xAxis = Axis().setCategories(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"))
//        yAxis = Axis().setTitle(Title("Temperature (C)"))
//        legend = Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(-10).setY(100).setBorderWidth(0)
//        addSeries(SimpleSeries().setName("Tokyo").setData(Arrays.asList(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)))
//        addSeries(SimpleSeries().setName("New York").setData(Arrays.asList(-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)))
//    }
//}
