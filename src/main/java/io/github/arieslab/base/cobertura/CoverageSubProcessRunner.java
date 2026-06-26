package io.github.arieslab.base.cobertura;

import org.jacoco.agent.AgentJar;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CoverageSubProcessRunner {

    private static final Logger LOGGER = Logger.getLogger(CoverageSubProcessRunner.class.getName());

    private CoverageSubProcessRunner() {}

    private static String jacocoAgentPath;
    private static File junitStandalonePath;

    static {
        try {
            jacocoAgentPath = AgentJar.extractToTempLocation().getAbsolutePath();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to extract JaCoCo agent", e);
        }
    }

    public static boolean runTests(File projectDir, String classpath) {
        if (jacocoAgentPath == null) {
            LOGGER.log(Level.SEVERE, "JaCoCo agent not available");
            return false;
        }

        if (junitStandalonePath == null || !junitStandalonePath.exists()) {
            junitStandalonePath = findJarOnClasspath(
                "org/junit/platform/console/ConsoleLauncher.class");
            if (junitStandalonePath == null) {
                LOGGER.log(Level.SEVERE, "JUnit Platform Console Standalone not found on classpath");
                return false;
            }
        }

        var jacocoExec = new File(projectDir, "target/jacoco.exec");
        jacocoExec.getParentFile().mkdirs();

        var javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        var cmd = new java.util.ArrayList<String>();
        cmd.add(javaBin);
        cmd.add("-javaagent:" + jacocoAgentPath + "=destfile=" + jacocoExec.getAbsolutePath());
        cmd.add("-cp");
        cmd.add(junitStandalonePath.getAbsolutePath() + File.pathSeparator + classpath);
        cmd.add("org.junit.platform.console.ConsoleLauncher");
        cmd.add("--scan-directory");
        cmd.add(new File(projectDir, "target/test-classes").getAbsolutePath());
        cmd.add("--disable-ansi-colors");
        cmd.add("--fail-if-no-tests");
        cmd.add("false");

        var pb = new ProcessBuilder(cmd);
        pb.directory(projectDir);
        pb.redirectErrorStream(true);

        LOGGER.log(Level.INFO, "Running coverage sub-process");

        try {
            var process = pb.start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            var exitCode = process.waitFor();
            LOGGER.log(Level.INFO, "Coverage sub-process exited with code: {0}", exitCode);
            return exitCode == 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Coverage sub-process failed", e);
            return false;
        }
    }

    private static File findJarOnClasspath(String className) {
        var url = CoverageSubProcessRunner.class.getClassLoader().getResource(className);
        if (url != null && "jar".equals(url.getProtocol())) {
            var path = url.getPath();
            var exclIndex = path.indexOf("!");
            if (exclIndex != -1) {
                var jarPath = path.substring(path.startsWith("file:") ? 5 : 0, exclIndex);
                var f = new File(jarPath);
                if (f.exists()) return f;
            }
        }

        var cp = System.getProperty("java.class.path");
        if (cp != null) {
            for (var entry : cp.split(File.pathSeparator)) {
                if (entry.contains(className.substring(0, className.lastIndexOf('/')))) {
                    return new File(entry);
                }
            }
        }

        return null;
    }
}
