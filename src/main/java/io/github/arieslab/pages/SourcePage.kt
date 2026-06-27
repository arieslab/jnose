package io.github.arieslab.pages

import io.github.arieslab.WicketApplication
import io.github.arieslab.core.testsmelldetector.testsmell.AbstractSmell
import io.github.arieslab.pages.base.ImprimirPage
import org.apache.wicket.markup.html.basic.Label
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger

class SourcePage(testSmell: AbstractSmell) : ImprimirPage() {

    companion object {
        private val LOGGER = Logger.getLogger(SourcePage::class.java.name)
    }

    init {
        add(Label("title", "Source: ${testSmell.smellName}"))

        var posURI = testSmell.javaClass.canonicalName
        posURI = posURI.replace(".", File.separator)
        posURI = "$posURI.java"

        val sourceString = readLineByLineJava8("${WicketApplication.JNOSE_PATH}/src/main/java/$posURI")

        add(Label("source", sourceString).apply { setEscapeModelStrings(false) })
    }

    private fun readLineByLineJava8(filePath: String): String {
        val contentBuilder = StringBuilder()
        try {
            Files.lines(Paths.get(filePath), StandardCharsets.UTF_8).use { stream ->
                stream.forEach { s -> contentBuilder.append(s).append("\n") }
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to read source file: $filePath - $e")
        }
        return contentBuilder.toString()
    }
}
