package io.github.arieslab.base

import org.apache.wicket.protocol.http.WebApplication
import java.io.BufferedWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.logging.Logger

object CSVCore {

    private val LOGGER = Logger.getLogger(CSVCore::class.java.name)
    private var outputFile: String = ""
    private var writer: BufferedWriter? = null
    private var pathAppToWebapp: String = ""
    private var reportPath: String = ""

    fun load(webApplication: WebApplication) {
        val realPath = webApplication.servletContext.getRealPath("")
        pathAppToWebapp = realPath ?: System.getProperty("user.dir")
        reportPath = "$pathAppToWebapp/reports/"
    }

    fun criarTestmappingdetectorCSV(todasLinhas: List<List<String>>, pastaDataHora: String, nomeProjeto: String): String {
        return criarCSV(todasLinhas, pastaDataHora, "${nomeProjeto}_testmappingdetector")
    }

    fun criarTestSmellsdetectorCSV(todasLinhas: List<List<String>>, pastaDataHora: String, nomeProjeto: String): String {
        return criarCSV(todasLinhas, pastaDataHora, "${nomeProjeto}_testsmesll")
    }

    fun criarCoberturaCSV(todasLinhas: List<List<String>>, pastaDataHora: String): String {
        return criarCSV(todasLinhas, pastaDataHora, "ClassInfor")
    }

    fun criarEvolution1CSV(todasLinhas: List<List<String>>, pastaDataHora: String, nomeProjeto: String): String {
        return criarCSV(todasLinhas, pastaDataHora, "${nomeProjeto}_testsmell_evolution1")
    }

    fun criarEvolution2CSV(todasLinhas: List<List<String>>, pastaDataHora: String, nomeProjeto: String): String {
        return criarCSV(todasLinhas, pastaDataHora, "${nomeProjeto}_testsmell_evolution2")
    }

    fun criarCSV(todasLinhas: List<List<String>>, pastaDataHora: String, nomeArquivo: String): String {
        val outFolder = "$reportPath$pastaDataHora"
        val outFile = "$reportPath$pastaDataHora/$nomeArquivo.csv"

        try {
            Files.createDirectories(Path.of(outFolder))
        } catch (e: Exception) {
            LOGGER.severe("Failed to create directory: $outFolder")
        }

        loadResultsWrite(outFile)

        for (linha in todasLinhas) {
            writeLine(linha.toMutableList())
        }

        try {
            writer?.close()
        } catch (e: Exception) {
            LOGGER.severe("Failed to close CSV file")
        }

        return outputFile
    }

    private fun loadResultsWrite(outputFile: String) {
        CSVCore.outputFile = outputFile
        try {
            Files.createDirectories(Path.of(outputFile).parent)
            writer = Files.newBufferedWriter(Path.of(outputFile), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        } catch (e: Exception) {
            LOGGER.severe("Failed to create CSV file: $outputFile")
        }
    }

    private fun writeLine(dataValues: MutableList<String>) {
        try {
            for (i in dataValues.indices) {
                if (dataValues[i].isEmpty()) {
                    dataValues[i] = ""
                }
                writer?.write(dataValues[i].replace("\n", "").replace("\r", ""))
                if (i != dataValues.size - 1) writer?.write(";") else writer?.write(System.lineSeparator())
            }
            writer?.flush()
        } catch (e: Exception) {
            LOGGER.severe("Failed to write CSV line")
        }
    }
}
