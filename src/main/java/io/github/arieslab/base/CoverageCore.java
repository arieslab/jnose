package io.github.arieslab.base;

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
            logRetorno.append(Util.dateNow() + "Resolvendo classpath...<br>");

            var classpath = CoverageClasspathResolver.resolve(projectDir);
            if (classpath.isEmpty()) {
                logRetorno.append(Util.dateNow() + " <font style='color:red'>Nenhuma classe compilada encontrada. Execute 'mvn test' primeiro.</font><br>");
                return null;
            }

            logRetorno.append(Util.dateNow() + "Executando testes com JaCoCo...<br>");
            CoverageSubProcessRunner.runTests(projectDir, classpath);

            var reportDir = new File(pastaPathReport + folderTime + File.separatorChar);
            var jacocoExec = new File(projectDir, "target/jacoco.exec");

            if (jacocoExec.exists()) {
                logRetorno.append(Util.dateNow() + "Gerando relatório de cobertura...<br>");
                var reportGen = new ReportGenerator(
                    new File(projeto.getPath()), reportDir);
                reportGen.create();

                var csvPath = reportDir + File.separator + projeto.getName() + "_jacoco.csv";
                logRetorno.append(Util.dateNow() + " <font style='color:green'>Relatório de cobertura gerado: " + csvPath + "</font><br>");
                return csvPath;
            } else {
                logRetorno.append(Util.dateNow() + " <font style='color:red'>Falha na cobertura (jacoco.exec não foi gerado)</font><br>");
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Coverage processing failed", e);
            logRetorno.append(Util.dateNow() + " <font style='color:red'>Erro: " + e.getMessage() + "</font><br>");
            return null;
        }
    }
}
