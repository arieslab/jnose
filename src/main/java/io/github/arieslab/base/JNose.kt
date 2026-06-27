package io.github.arieslab.base

import io.github.arieslab.WicketApplication
import io.github.arieslab.core.Config
import io.github.arieslab.core.JNoseCore
import io.github.arieslab.core.testsmelldetector.testsmell.TestSmellDetector
import io.github.arieslab.dto.TestSmell
import io.github.arieslab.dtolocal.Commit
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.dto.TestClass
import io.github.arieslab.dtolocal.TotalProcessado
import java.io.File
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.logging.Logger

object JNose {

    private val LOGGER = Logger.getLogger(JNose::class.java.name)
    private var jNoseCore: JNoseCore? = null

    private val config: Config = object : Config {
        override fun assertionRoulette() = TestSmellDetectorConfig.assertionRoulette
        override fun conditionalTestLogic() = TestSmellDetectorConfig.conditionalTestLogic
        override fun constructorInitialization() = TestSmellDetectorConfig.constructorInitialization
        override fun defaultTest() = TestSmellDetectorConfig.defaultTest
        override fun dependentTest() = TestSmellDetectorConfig.dependentTest
        override fun duplicateAssert() = TestSmellDetectorConfig.duplicateAssert
        override fun eagerTest() = TestSmellDetectorConfig.eagerTest
        override fun emptyTest() = TestSmellDetectorConfig.emptyTest
        override fun exceptionCatchingThrowing() = TestSmellDetectorConfig.exceptionCatchingThrowing
        override fun generalFixture() = TestSmellDetectorConfig.generalFixture
        override fun mysteryGuest() = TestSmellDetectorConfig.mysteryGuest
        override fun printStatement() = TestSmellDetectorConfig.printStatement
        override fun redundantAssertion() = TestSmellDetectorConfig.redundantAssertion
        override fun sensitiveEquality() = TestSmellDetectorConfig.sensitiveEquality
        override fun verboseTest() = TestSmellDetectorConfig.verboseTest
        override fun sleepyTest() = TestSmellDetectorConfig.sleepyTest
        override fun lazyTest() = TestSmellDetectorConfig.lazyTest
        override fun unknownTest() = TestSmellDetectorConfig.unknownTest
        override fun ignoredTest() = TestSmellDetectorConfig.ignoredTest
        override fun resourceOptimism() = TestSmellDetectorConfig.resourceOptimism
        override fun magicNumberTest() = TestSmellDetectorConfig.magicNumberTest
        override fun maxStatements() = 30
    }

    fun getInstanceJNoseCore(): JNoseCore {
        if (jNoseCore == null) {
            jNoseCore = JNoseCore(config)
        }
        return jNoseCore!!
    }

    init {
        jNoseCore = getInstanceJNoseCore()
    }

    fun convert(listTestClass: List<TestClass>): MutableList<MutableList<String>> {
        val todasLinhas = mutableListOf<MutableList<String>>()

        var columnValues = mutableListOf(
            "projectName", "name", "pathFile", "productionFile", "junitVersion",
            "loc", "qtdMethods", "testSmellName", "testSmellMethod",
            "testSmellLineBegin", "testSmellLineEnd", "methodNameHash",
            "methodNameFullHash", "methodCode", "methodCodeHash", "FullHash"
        )
        todasLinhas.add(columnValues)

        for (testClass in listTestClass) {
            for (testSmell in testClass.listTestSmell) {
                columnValues = mutableListOf()
                columnValues.add(testClass.projectName)
                columnValues.add(testClass.name)
                columnValues.add(testClass.pathFile)
                columnValues.add(testClass.productionFile)
                columnValues.add(testClass.junitVersion.name)
                columnValues.add(testClass.numberLine.toString())
                columnValues.add(testClass.numberMethods.toString())
                columnValues.add(testSmell.name)
                columnValues.add(testSmell.method)
                columnValues.add(testSmell.range)
                columnValues.add(testSmell.range)
                columnValues.add(testSmell.methodNameHash)
                columnValues.add(testSmell.methodNameFullURIHash)

                if (testSmell.range.contains("-")) {
                    val start = testSmell.range.replace(" ", "").split("-")[0].toInt()
                    val end = testSmell.range.replace(" ", "").split("-")[1].toInt()
                    var code_ = getSource(testClass.pathFile, start, end)
                    code_ = code_.replace("/\\r?\\n|\\r/", "")
                    columnValues.add(code_)
                    columnValues.add(hash(code_))
                    columnValues.add(hash(code_ + testSmell.methodNameFullURIHash))
                } else if (testSmell.range.contains(",")) {
                    val lista = testSmell.range.split(",")
                    var code_ = getSource(testClass.pathFile, *lista.toTypedArray())
                    code_ = code_.replace("/\\r?\\n|\\r/", "")
                    columnValues.add(code_)
                    columnValues.add(hash(code_))
                    columnValues.add(hash(code_ + testSmell.methodNameFullURIHash))
                }

                todasLinhas.add(columnValues)
            }
        }

        return todasLinhas
    }

    fun hash(string: String): String {
        return try {
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(StandardCharsets.UTF_8.encode(string))
            "%032x".format(BigInteger(1, md5.digest()))
        } catch (e: Exception) {
            LOGGER.severe("Failed to hash: $e")
            ""
        }
    }

    private fun getSource(pathFile: String, start: Int, end: Int): String {
        val stringBuilder = StringBuilder()
        val path = Paths.get(pathFile)
        var s = start
        var e = end
        if (s >= 1) s--
        if (e >= 1) e--

        try {
            val lines = Files.readAllLines(path)
            for (i in s..e) {
                val textoLinha = lines[i].trim().replace(" ", "").replace(";", "|")
                stringBuilder.append(textoLinha)
            }
        } catch (ex: Exception) {
            LOGGER.warning("Failed to read source: $pathFile - $ex")
        }
        return stringBuilder.toString()
    }

    private fun getSource(pathFile: String, vararg lista: String): String {
        val stringBuilder = StringBuilder()
        val path = Paths.get(pathFile)
        try {
            val lines = Files.readAllLines(path)
            for (i in lista) {
                var number = i.trim().toInt()
                if (number >= 1) number--
                val textoLinha = lines[number].replace(" ", "").replace(";", "|")
                stringBuilder.append(textoLinha)
            }
        } catch (ex: Exception) {
            LOGGER.warning("Failed to read source: $pathFile - $ex")
        }
        return stringBuilder.toString()
    }

    fun getJUnitVersion(directoryPath: String): TestClass.JunitVersion {
        return getInstanceJNoseCore().getJUnitVersion(directoryPath)
    }

    fun processarTestSmellDetector2(pathProjeto: String, logRetorno: StringBuffer): MutableList<MutableList<String>> {
        val nameProjeto = pathProjeto.substring(pathProjeto.lastIndexOf(File.separatorChar) + 1)
        logRetorno.insert(0, "${Util.dateNow()}$nameProjeto - <font style='color:yellow'>TestSmellDetector novo</font> <br>")
        val todasLinhas = mutableListOf<MutableList<String>>()

        try {
            val listFileTests = getInstanceJNoseCore().getFilesTest(pathProjeto)
            val linhacolunas = mutableListOf<String>()
            linhacolunas.add("App")
            linhacolunas.add("TestFileName")
            linhacolunas.add("PathFile")
            linhacolunas.add("ProductionFileName")
            linhacolunas.add("LOC")
            linhacolunas.add("numberMethods")
            listFileTests[0].lineSumTestSmells.keys.forEach { linhacolunas.add(it) }
            todasLinhas.add(linhacolunas)

            for (testClass in listFileTests) {
                val linha = mutableListOf<String>()
                linha.add(testClass.projectName)
                linha.add(testClass.name)
                linha.add(testClass.pathFile)
                linha.add(testClass.productionFile)
                linha.add(testClass.numberLine.toString())
                linha.add(testClass.numberMethods.toString())
                testClass.lineSumTestSmells.values.forEach { linha.add(it.toString()) }
                todasLinhas.add(linha)
            }
        } catch (e: Exception) {
            LOGGER.severe("Failed to process test smell detector for: $pathProjeto - $e")
        }

        return todasLinhas
    }

    @Throws(Exception::class)
    fun processarProjeto(projeto: ProjetoDTO): List<TestClass> {
        projeto.procentagem =(25)
        val listaTestClass = getInstanceJNoseCore().getFilesTest(projeto.path)
        projeto.procentagem =(100)
        projeto.processado = true
        projeto.processado2 =(true)
        return listaTestClass
    }

    @Throws(java.io.IOException::class)
    fun processarProjeto2(
        projeto: ProjetoDTO,
        valorProcProject: Float,
        folderTime: String,
        totalProcessado: TotalProcessado,
        pastaPathReport: String,
        logRetorno: StringBuffer
    ): MutableList<MutableList<String>> {
        logRetorno.insert(0, "${Util.dateNow()}${projeto.name} - started <br>")
        val valorSoma = valorProcProject / 4

        totalProcessado.setValor(5)
        projeto.procentagem =(totalProcessado.valor)

        if (WicketApplication.COBERTURA_ON) {
            val csvPath = CoverageCore.processarCobertura(projeto, folderTime, pastaPathReport, logRetorno)
            projeto.coverageCsvPath = csvPath
        }

        projeto.processado2 =(true)
        totalProcessado.setValor(totalProcessado.valor + valorSoma.toInt())
        projeto.procentagem =(25)

        totalProcessado.setValor(totalProcessado.valor + valorSoma.toInt())
        projeto.procentagem =(50)

        totalProcessado.setValor(totalProcessado.valor + valorSoma.toInt())
        projeto.procentagem =(75)

        val todasLinhas = processarTestSmellDetector2(projeto.path, logRetorno)
        totalProcessado.setValor(totalProcessado.valor + valorSoma.toInt())
        projeto.procentagem =(100)

        projeto.processado = true
        return todasLinhas
    }

    fun processarProjetos2(
        lista: List<ProjetoDTO>,
        folderTime: String,
        totalProcessado: TotalProcessado,
        pastaPathReport: String,
        logRetorno: StringBuffer
    ): MutableList<MutableList<String>> {
        val success = File("$pastaPathReport$folderTime${File.separatorChar}").mkdirs()
        if (!success) LOGGER.fine("Created Folder...")

        totalProcessado.setValor(0)

        val totalLista = lista.size
        val valorSoma = if (totalLista > 0) 100 / totalLista else 0

        val listaTodos = mutableListOf<MutableList<String>>()

        for (projeto in lista) {
            Thread {
                try {
                    val todasLinhas = processarProjeto2(projeto, valorSoma.toFloat(), folderTime, totalProcessado, pastaPathReport, logRetorno)
                    projeto.resultado =(todasLinhas)
                    listaTodos.addAll(todasLinhas)
                } catch (e: Exception) {
                    LOGGER.severe("Failed processing project: ${projeto.name} - $e")
                    projeto.bugs = "${projeto.bugs}\n${e.message}"
                }
            }.start()
        }

        return listaTodos
    }

    fun processarProjetos(
        lista: List<ProjetoDTO>,
        folderTime: String,
        pastaPathReport: String,
        totalProcessado: TotalProcessado
    ) {
        val success = File("$pastaPathReport$folderTime${File.separatorChar}").mkdirs()
        if (!success) LOGGER.fine("Created Folder...")

        totalProcessado.setValor(0)

        val totalLista = lista.size
        val valorSoma = if (totalLista > 0) 100 / totalLista else 0

        for (projeto in lista) {
            try {
                val todasLinhas = processarProjeto(projeto)
                projeto.resultadoByTestSmells =(todasLinhas as MutableList<TestClass>)
                projeto.resultado =(convert(todasLinhas))
            } catch (e: Exception) {
                LOGGER.severe("Failed processing project: ${projeto.name} - $e")
            }
        }
    }

    fun processarEvolution(
        projeto: ProjetoDTO,
        logRetorno: StringBuffer,
        mapa: MutableMap<Int, MutableList<MutableList<String>>>
    ) {
        val DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        GitCore.checkout("master", projeto.path)

        val lista = when {
            "commit".equals(projeto.optionSelected, ignoreCase = true) -> projeto.listaCommits
            "tag".equals(projeto.optionSelected, ignoreCase = true) -> projeto.listaTags
            else -> mutableListOf<Commit>()
        }

        if (lista.isNullOrEmpty()) {
            logRetorno.insert(0, "Nenhum commit ou tag selecionado<br>")
            return
        }

        lista.sortWith(Comparator.nullsLast(compareBy<Commit> { it.date }))

        val todasLinhas1 = mutableListOf<MutableList<String>>()
        val todasLinhas2 = mutableListOf<MutableList<String>>()
        val todasLinhas3 = mutableListOf<MutableList<String>>()
        val todasLinhas4 = mutableListOf<MutableList<String>>()
        val todasLinhas5 = mutableListOf<MutableList<String>>()

        var cont = 1
        var primeiraLinha = true
        val jaProcessado = mutableSetOf<String>()

        val core = getInstanceJNoseCore()
        val numberThread = Runtime.getRuntime().availableProcessors() * 2
        val threadpool = Executors.newFixedThreadPool(numberThread)

        try {
            for (commit in lista) {
                logRetorno.insert(0, "${cont++} - Analyze commit: ${commit.id}<br>")

                try {
                    GitCore.checkout(commit.id, projeto.path)

                    val listTestClass = core.getFilesTest(projeto.path, threadpool)

                    if (listTestClass.isNotEmpty() && primeiraLinha) {
                        primeiraLinha = false
                        val listaColumName = mutableListOf(
                            "commit.id()", "commit.authorName()", "commit.date()",
                            "commit.msg()", "commit.tag()", "project", "TestClass",
                            "PathFile", "ProductionFile", "NumberLine", "NumberMethods"
                        )
                        listaColumName.addAll(TestSmellDetector.getAllTestSmellNames())
                        todasLinhas1.add(listaColumName)
                    }

                    var totalTestSmells = 0

                    for (testClass in listTestClass) {
                        val lista3 = mutableListOf(
                            commit.id,
                            commit.authorName,
                            commit.date?.toInstant()?.atZone(ZoneId.systemDefault())?.format(DATE_FMT) ?: "",
                            commit.msg,
                            commit.tag ?: "",
                            projeto.name,
                            testClass.name,
                            testClass.pathFile,
                            testClass.productionFile,
                            testClass.numberLine.toString(),
                            testClass.numberMethods.toString()
                        )
                        testClass.lineSumTestSmells.values.forEach { lista3.add(it.toString()) }
                        todasLinhas1.add(lista3)

                        for (ts in testClass.listTestSmell) {
                            val sha256 = Util.getSHA5Code(testClass, ts).trim()

                            if (jaProcessado.add(sha256)) {
                                totalTestSmells++

                                val lista4 = mutableListOf(
                                    commit.id,
                                    commit.authorName,
                                    commit.date?.toInstant()?.atZone(ZoneId.systemDefault())?.format(DATE_FMT) ?: "",
                                    commit.msg,
                                    commit.tag ?: "",
                                    projeto.name,
                                    testClass.name,
                                    testClass.pathFile,
                                    testClass.productionFile,
                                    testClass.numberLine.toString(),
                                    testClass.numberMethods.toString(),
                                    ts.name,
                                    ts.method,
                                    ts.range,
                                    sha256
                                )
                                todasLinhas3.add(lista4)
                            }
                        }
                    }

                    val lista2 = mutableListOf(
                        commit.id,
                        commit.tag ?: "",
                        commit.date?.toInstant()?.atZone(ZoneId.systemDefault())?.format(DATE_FMT) ?: "",
                        totalTestSmells.toString()
                    )
                    todasLinhas2.add(lista2)

                } catch (e: Exception) {
                    LOGGER.severe("Failed processing evolution for commit: ${commit.id} - $e")
                }
            }

            mapa[1] = todasLinhas1
            mapa[2] = todasLinhas2
            mapa[3] = todasLinhas3

            val setSHA = mutableSetOf<String>()
            val mapName = mutableMapOf<String, Int>()

            for (linha in todasLinhas3) {
                if (setSHA.add(linha[14])) {
                    todasLinhas4.add(linha)
                }
                mapName.merge(linha[1], 1, Int::plus)
            }

            mapa[4] = todasLinhas4

            mapName.forEach { (s, i) ->
                val lista3 = mutableListOf(s, i.toString())
                todasLinhas5.add(lista3)
            }

            mapa[5] = todasLinhas5

            GitCore.checkout("master", projeto.path)
        } finally {
            threadpool.shutdown()
        }
    }
}
