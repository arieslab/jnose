package io.github.arieslab.pages

import io.github.arieslab.base.CSVCore
import io.github.arieslab.base.Util
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.pages.base.ImprimirPage
import org.apache.wicket.AttributeModifier
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.DownloadLink
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import java.io.File

class ResultPage2(
    projeto: ProjetoDTO,
    todasLinhas: MutableList<MutableList<String>>,
    title: String,
    csvFileName: String,
    fontColorized: Boolean
) : ImprimirPage() {

    init {
        add(Label("title", "$title - View Sample (100 of ${todasLinhas.size})"))

        add(DownloadLink("lkEsportCSV", gerarCSV(todasLinhas, csvFileName)))

        val todasLinhasSample = if (todasLinhas.size > 101) todasLinhas.subList(0, 101) else todasLinhas

        add(object : ListView<List<String>>("lista", todasLinhasSample) {
            override fun populateItem(item: ListItem<List<String>>) {
                val linha = item.modelObject
                for (i in 0..10) {
                    lb(i, linha, item, fontColorized)
                }

                if (linha[9].contains("-") || linha[9].contains(",") || Util.isInt(linha[9])) {
                    val testsmellName = linha[7]
                    val pathFileTest = linha[2]
                    val range = linha[9]
                    item.add(object : Link<String>("codigo") {
                        override fun onClick() {
                            setResponsePage(CodePage(projeto, testsmellName, pathFileTest, range))
                        }
                    })
                } else {
                    item.add(object : Link<String>("codigo") {
                        override fun onClick() {}
                    }.apply { isVisible = false })
                }
            }
        })
    }

    private fun gerarCSV(todasLinhas: List<List<String>>, csvFileName: String): File {
        val pathFile = CSVCore.criarCSV(todasLinhas, Util.dateNowFolder(), csvFileName)
        return File(pathFile)
    }

    private fun lb(numero: Int, linha: List<String>, item: ListItem<List<String>>, fontColorized: Boolean) {
        if (linha.size > numero) {
            val lb = Label("lb$numero", linha[numero])
            if (Util.isInt(linha[numero])) {
                val n = linha[numero].toInt()
                if (fontColorized) {
                    lb.add(if (n != 0) AttributeModifier("style", "color:red") else AttributeModifier("style", "color:green"))
                }
            }
            item.add(lb)
        } else {
            item.add(Label("lb$numero").apply { isVisible = false })
        }
    }
}
