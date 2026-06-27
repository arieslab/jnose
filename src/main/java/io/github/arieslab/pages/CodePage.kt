package io.github.arieslab.pages

import io.github.arieslab.base.GitCore
import io.github.arieslab.base.Util
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.pages.base.ImprimirPage
import org.apache.wicket.behavior.AttributeAppender
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger

class CodePage(projeto: ProjetoDTO, title: String, pathFile: String, range: String) : ImprimirPage() {

    companion object {
        private val LOGGER = Logger.getLogger(CodePage::class.java.name)
    }

    init {
        add(Label("title", "Code Test Smell: $title [$range]"))

        val mapaBlame = GitCore.blame(projeto.path, pathFile)

        val linhas = mutableListOf<String>()

        try {
            Files.newBufferedReader(Path.of(pathFile), StandardCharsets.UTF_8).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    linhas.add(line!!)
                }
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to read file: $pathFile - $e")
        }

        var cont = 1
        add(object : ListView<String>("lvCodigo", linhas) {
            override fun populateItem(item: ListItem<String>) {
                val linha = item.modelObject
                item.add(Label("numero", cont))
                item.add(Label("blame", mapaBlame[cont]))

                val container = WebMarkupContainer("container")

                when {
                    range.contains("-") -> {
                        val ranger2 = range.split("-")
                        val inicio = ranger2[0].trim().toInt()
                        val fim = ranger2[1].trim().toInt()
                        if (cont in inicio..fim) {
                            item.add(AttributeAppender("style", "background-color: #ffe6ff;"))
                        }
                    }
                    range.contains(",") -> {
                        val ranger2 = range.replace(" ", "").split(",")
                        if (ranger2.contains(cont.toString())) {
                            item.add(AttributeAppender("style", "background-color: #ffe6ff;"))
                        }
                    }
                    Util.isInt(range.trim()) -> {
                        val range2 = range.trim().toInt()
                        if (range2 == cont) {
                            item.add(AttributeAppender("style", "background-color: #ffe6ff;"))
                        }
                    }
                }

                container.add(Label("codigo", linha))
                item.add(container)

                cont++
            }
        })
    }
}
