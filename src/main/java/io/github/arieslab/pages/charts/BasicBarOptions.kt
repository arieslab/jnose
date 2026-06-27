//package io.github.arieslab.pages.charts
//
//import de.adesso.wickedcharts.highcharts.options.*
//import de.adesso.wickedcharts.highcharts.options.color.HexColor
//import de.adesso.wickedcharts.highcharts.options.series.SimpleSeries
//
//class BasicBarOptions : Options() {
//    init {
//        chartOptions = ChartOptions().setType(SeriesType.BAR)
//        global = Global().setUseUTC(true)
//        title = Title("Historic World Population by Region")
//        subtitle = Title("Source: Wikipedia.org")
//        xAxis = Axis().setCategories("Africa", "America", "Asia", "Europe", "Oceania").setTitle(Title(null))
//        yAxis = Axis().setTitle(Title("Population (millions)").setAlign(HorizontalAlignment.HIGH)).setLabels(Labels().setOverflow(Overflow.JUSTIFY))
//        tooltip = Tooltip().setFormatter(Function("return ''+this.series.name +': '+ this.y +' millions';"))
//        plotOptions = PlotOptionsChoice().setBar(PlotOptions().setDataLabels(DataLabels().setEnabled(true)))
//        legend = Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(-100).setY(100).setFloating(true).setBorderWidth(1).setBackgroundColor(HexColor("#ffffff")).setShadow(true)
//        credits = CreditOptions().setEnabled(false)
//        addSeries(SimpleSeries().setName("Year 1800").setData(107, 31, 635, 203, 2))
//        addSeries(SimpleSeries().setName("Year 1900").setData(133, 156, 947, 408, 6))
//        addSeries(SimpleSeries().setName("Year 2008").setData(973, 914, 4054, 732, 34))
//    }
//
//    fun getLabel(): String = "Basic bar"
//}
