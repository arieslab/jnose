package io.github.arieslab.base

import io.github.arieslab.dto.TestClass
import io.github.arieslab.dto.TestSmell
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HexFormat
import java.util.logging.Logger

object Util {

    private val LOGGER = Logger.getLogger(Util::class.java.name)

    fun isInt(s: String): Boolean {
        return try {
            Integer.parseInt(s)
            true
        } catch (_: NumberFormatException) {
            false
        }
    }

    fun dateNow(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")) + " - "
    }

    fun dateNowFolder(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    }

    fun execCommand(commandLine: String, pathExecute: String) {
        try {
            val pb = ProcessBuilder(*commandLine.split(" ".toRegex()).toTypedArray())
            pb.directory(java.io.File(pathExecute))
            pb.redirectErrorStream(true)
            val process = pb.start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var lineOut: String?
                while (reader.readLine().also { lineOut = it } != null) {
                    LOGGER.info(lineOut)
                }
            }
            process.waitFor()
        } catch (e: Exception) {
            LOGGER.severe("Command failed: $commandLine")
        }
    }

    fun getCode(testClass: TestClass, testSmell: TestSmell): String {
        val nomeClassPath = testClass.pathFile
        val range = testSmell.range

        val linhasComTestSmells = mutableListOf<Int>()

        if (range.contains("-")) {
            val ranger2 = range.split("-")
            val inicio = ranger2[0].trim().toInt()
            val fim = ranger2[1].trim().toInt()
            for (i in inicio..fim) {
                linhasComTestSmells.add(i)
            }
        } else if (range.contains(",")) {
            val ranger2 = range.replace(" ", "").split(",")
            for (linha in ranger2) {
                linhasComTestSmells.add(linha.toInt())
            }
        } else if (isInt(range.trim())) {
            val range2 = range.trim().toInt()
            linhasComTestSmells.add(range2)
        }

        val linhasStringComSmells = mutableListOf<String>()

        try {
            Files.newBufferedReader(Path.of(nomeClassPath), StandardCharsets.UTF_8).use { reader ->
                var line: String?
                var contLinha = 1
                while (reader.readLine().also { line = it } != null) {
                    if (linhasComTestSmells.contains(contLinha)) {
                        linhasStringComSmells.add(line!!)
                    }
                    contLinha++
                }
            }
        } catch (e: Exception) {
            LOGGER.severe("Failed to read file: $nomeClassPath")
        }

        return linhasStringComSmells.toString()
    }

    fun getSHA5Code(testClass: TestClass, testSmell: TestSmell): String {
        val code = getCode(testClass, testSmell)
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedhash = digest.digest(code.toByteArray(StandardCharsets.UTF_8))
        return bytesToHex(encodedhash)
    }

    private fun bytesToHex(hash: ByteArray): String {
        return HexFormat.of().formatHex(hash)
    }
}
