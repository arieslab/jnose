package io.github.arieslab.base.cobertura

import org.jacoco.agent.AgentJar
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.logging.Logger

object CoverageSubProcessRunner {

    private val LOGGER = Logger.getLogger(CoverageSubProcessRunner::class.java.name)
    private var jacocoAgentPath: String? = null
    private var junitStandalonePath: File? = null

    init {
        try {
            jacocoAgentPath = AgentJar.extractToTempLocation().absolutePath
        } catch (e: Exception) {
            LOGGER.severe("Failed to extract JaCoCo agent: $e")
        }
    }

    fun runTests(projectDir: File, classpath: String): Boolean {
        return runTests(projectDir, classpath, BuildExecutor.detect(projectDir))
    }

    fun runTests(projectDir: File, classpath: String, type: BuildType): Boolean {
        if (jacocoAgentPath == null) {
            LOGGER.severe("JaCoCo agent not available")
            return false
        }

        if (junitStandalonePath == null || !junitStandalonePath!!.exists()) {
            junitStandalonePath = findJarOnClasspath("org/junit/platform/console/ConsoleLauncher.class")
            if (junitStandalonePath == null) {
                LOGGER.severe("JUnit Platform Console Standalone not found on classpath")
                return false
            }
        }

        val jacocoExec = type.getJacocoExecFile(projectDir)
        jacocoExec.parentFile.mkdirs()

        val javaBin = "${System.getProperty("java.home")}${File.separator}bin${File.separator}java"

        val cmd = mutableListOf<String>()
        cmd.add(javaBin)
        cmd.add("-javaagent:${jacocoAgentPath}=destfile=${jacocoExec.absolutePath}")
        cmd.add("-cp")
        cmd.add("${junitStandalonePath!!.absolutePath}${File.pathSeparator}$classpath")
        cmd.add("org.junit.platform.console.ConsoleLauncher")
        cmd.add("--scan-directory")
        cmd.add(type.getTestClassesDir(projectDir).absolutePath)
        cmd.add("--disable-ansi-colors")
        cmd.add("--fail-if-no-tests")
        cmd.add("false")

        val pb = ProcessBuilder(cmd)
        pb.directory(projectDir)
        pb.redirectErrorStream(true)

        LOGGER.info("Running coverage sub-process")

        return try {
            val process = pb.start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    println(line)
                }
            }
            val exitCode = process.waitFor()
            LOGGER.info("Coverage sub-process exited with code: $exitCode")
            exitCode == 0
        } catch (e: Exception) {
            LOGGER.severe("Coverage sub-process failed: $e")
            false
        }
    }

    private fun findJarOnClasspath(className: String): File? {
        val url = CoverageSubProcessRunner::class.java.classLoader.getResource(className)
        if (url != null && "jar" == url.protocol) {
            val path = url.path
            val exclIndex = path.indexOf("!")
            if (exclIndex != -1) {
                val jarPath = path.substring(if (path.startsWith("file:")) 5 else 0, exclIndex)
                val f = File(jarPath)
                if (f.exists()) return f
            }
        }

        val cp = System.getProperty("java.class.path")
        if (cp != null) {
            for (entry in cp.split(File.pathSeparator)) {
                if (entry.contains(className.substring(0, className.lastIndexOf('/')))) {
                    return File(entry)
                }
            }
        }

        return null
    }
}
