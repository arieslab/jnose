package io.github.arieslab.base;

import io.github.arieslab.base.cobertura.ReportGenerator;
import io.github.arieslab.dtolocal.ProjetoDTO;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoverageCore {

    private static final Logger LOGGER = Logger.getLogger(CoverageCore.class.getName());

    public static void processarCobertura(ProjetoDTO projeto, String folderTime, String pastaPathReport, StringBuffer logRetorno) {
        logRetorno.append(Util.dateNow() + projeto.getName() + " - <font style='color:blue'>Coverage</font> <br>");
        try {
            Util.execCommand("mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Drat.skip=true", projeto.getPath());
            ReportGenerator reportGenerator = new ReportGenerator(new File(projeto.getPath()), new File(pastaPathReport + folderTime + File.separatorChar));
            reportGenerator.create();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Coverage processing failed", e);
        }
    }

}
