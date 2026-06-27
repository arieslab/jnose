package io.github.arieslab.base;

import io.github.arieslab.base.cobertura.BuildExecutor;
import io.github.arieslab.base.cobertura.CoverageClasspathResolver;
import io.github.arieslab.base.cobertura.CoverageSubProcessRunner;
import io.github.arieslab.base.cobertura.ReportGenerator;
import io.github.arieslab.dtolocal.ProjetoDTO;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoverageCore {

    private static final Logger LOGGER = Logger.getLogger(CoverageCore.class.getName());

    public static String processarCobertura(ProjetoDTO projeto, String folderTime, String pastaPathReport, StringBuffer logRetorno) {
        logRetorno.append(Util.dateNow() + projeto.getName() + " - <font style='color:blue'>Coverage</font> <br>");
        try {
            var projectDir = new File(projeto.getPath());
            var buildType = BuildExecutor.detect(projectDir);

            logRetorno.append(Util.dateNow() + "Build detectado: " + buildType + "<br>");

            logRetorno.append(Util.dateNow() + "Compilando projeto...<br>");
            var compiled = BuildExecutor.compile(projectDir);
            if (!compiled) {
                logRetorno.append(Util.dateNow() + " <font style='color:orange'>Aviso: compilação pode ter falhado, tentando continuar...</font><br>");
            } else {
                logRetorno.append(Util.dateNow() + " <font style='color:green'>Compilação concluída</font><br>");
            }

            logRetorno.append(Util.dateNow() + "Resolvendo classpath...<br>");
            var classpath = CoverageClasspathResolver.resolve(projectDir, buildType);
            if (classpath.isEmpty()) {
                logRetorno.append(Util.dateNow() + " <font style='color:red'>Nenhuma classe compilada encontrada.</font><br>");
                return null;
            }

            logRetorno.append(Util.dateNow() + "Executando testes com JaCoCo...<br>");
            CoverageSubProcessRunner.runTests(projectDir, classpath, buildType);

            var jacocoExec = buildType.getJacocoExecFile(projectDir);
            if (jacocoExec.exists()) {
                logRetorno.append(Util.dateNow() + "Gerando relatório de cobertura...<br>");
                var reportGen = new ReportGenerator(
                    new File(projeto.getPath()),
                    new File(pastaPathReport + folderTime + File.separatorChar),
                    buildType);
                reportGen.create();

                var csvPath = pastaPathReport + folderTime + File.separatorChar + projeto.getName() + "_jacoco.csv";
                logRetorno.append(Util.dateNow() + " <font style='color:green'>Relatório de cobertura gerado: " + csvPath + "</font><br>");
                return csvPath;
            } else {
                logRetorno.append(Util.dateNow() + " <font style='color:red'>Falha na cobertura (jacoco.exec não foi gerado em " + jacocoExec + ")</font><br>");
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Coverage processing failed", e);
            logRetorno.append(Util.dateNow() + " <font style='color:red'>Erro: " + e.getMessage() + "</font><br>");
            return null;
        }
    }
}
