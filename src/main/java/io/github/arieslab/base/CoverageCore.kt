package io.github.arieslab.base

import io.github.arieslab.base.cobertura.BuildExecutor
import io.github.arieslab.base.cobertura.CoverageClasspathResolver
import io.github.arieslab.base.cobertura.CoverageSubProcessRunner
import io.github.arieslab.base.cobertura.ReportGenerator
import io.github.arieslab.dtolocal.ProjetoDTO
import java.io.File
import java.util.logging.Logger

object CoverageCore {

    private val LOGGER = Logger.getLogger(CoverageCore::class.java.name)

    fun processarCobertura(projeto: ProjetoDTO, folderTime: String, pastaPathReport: String, logRetorno: StringBuffer): String? {
        logRetorno.append("${Util.dateNow()}${projeto.name} - <font style='color:blue'>Coverage</font> <br>")
        return try {
            val projectDir = File(projeto.path)
            val buildType = BuildExecutor.detect(projectDir)

            logRetorno.append("${Util.dateNow()}Build detectado: $buildType<br>")
            logRetorno.append("${Util.dateNow()}Compilando projeto...<br>")
            val compiled = BuildExecutor.compile(projectDir)
            if (!compiled) {
                logRetorno.append("${Util.dateNow()} <font style='color:orange'>Aviso: compilação pode ter falhado, tentando continuar...</font><br>")
            } else {
                logRetorno.append("${Util.dateNow()} <font style='color:green'>Compilação concluída</font><br>")
            }

            logRetorno.append("${Util.dateNow()}Resolvendo classpath...<br>")
            val classpath = CoverageClasspathResolver.resolve(projectDir, buildType)
            if (classpath.isEmpty()) {
                logRetorno.append("${Util.dateNow()} <font style='color:red'>Nenhuma classe compilada encontrada.</font><br>")
                return null
            }

            logRetorno.append("${Util.dateNow()}Executando testes com JaCoCo...<br>")
            CoverageSubProcessRunner.runTests(projectDir, classpath, buildType)

            val jacocoExec = buildType.getJacocoExecFile(projectDir)
            if (jacocoExec.exists()) {
                logRetorno.append("${Util.dateNow()}Gerando relatório de cobertura...<br>")
                val reportGen = ReportGenerator(
                    File(projeto.path),
                    File("$pastaPathReport$folderTime${File.separatorChar}"),
                    buildType)
                reportGen.create()

                val csvPath = "$pastaPathReport$folderTime${File.separatorChar}${projeto.name}_jacoco.csv"
                logRetorno.append("${Util.dateNow()} <font style='color:green'>Relatório de cobertura gerado: $csvPath</font><br>")
                csvPath
            } else {
                logRetorno.append("${Util.dateNow()} <font style='color:red'>Falha na cobertura (jacoco.exec não foi gerado em $jacocoExec)</font><br>")
                null
            }
        } catch (e: Exception) {
            LOGGER.severe("Coverage processing failed: $e")
            logRetorno.append("${Util.dateNow()} <font style='color:red'>Erro: ${e.message}</font><br>")
            null
        }
    }
}
