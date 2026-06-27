package io.github.arieslab.base.cobertura

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.logging.Logger

object BuildExecutor {

    private val LOGGER = Logger.getLogger(BuildExecutor::class.java.name)

    fun detect(projectDir: File): BuildType {
        if (File(projectDir, "pom.xml").exists()) return BuildType.MAVEN
        if (File(projectDir, "build.gradle").exists() || File(projectDir, "build.gradle.kts").exists()) return BuildType.GRADLE
        if (File(projectDir, "build.xml").exists()) return BuildType.ANT
        if (File(projectDir, "build.sbt").exists()) return BuildType.SBT
        return BuildType.UNKNOWN
    }

    fun compile(projectDir: File): Boolean {
        val type = detect(projectDir)
        val cmd = buildCommand(projectDir, type)
        if (cmd == null || cmd.isEmpty()) {
            LOGGER.warning("No compile command for build type: $type")
            return false
        }

        LOGGER.info("Compiling project with: $cmd")

        val pb = ProcessBuilder(cmd)
        pb.directory(projectDir)
        pb.redirectErrorStream(true)

        return try {
            val process = pb.start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    println(line)
                }
            }
            val exitCode = process.waitFor()
            LOGGER.info("Compilation exit code: $exitCode")
            exitCode == 0
        } catch (e: Exception) {
            LOGGER.severe("Compilation failed: $e")
            false
        }
    }

    private fun buildCommand(projectDir: File, type: BuildType): List<String>? {
        return when (type) {
            BuildType.MAVEN -> {
                val mvn = findTool(projectDir, "mvnw", "mvn")
                listOf(mvn, "test-compile")
            }
            BuildType.GRADLE -> {
                val gradle = findTool(projectDir, "gradlew", "gradle")
                listOf(gradle, "testClasses")
            }
            BuildType.ANT -> {
                val ant = findTool("ant")
                ant?.let { listOf(it) }
            }
            BuildType.SBT -> {
                val sbt = findTool("sbt")
                sbt?.let { listOf(it, "test:compile") }
            }
            BuildType.UNKNOWN -> {
                val javac = findTool("javac")
                if (javac != null) {
                    listOf(javac, "-d", "bin", findSources(projectDir))
                } else null
            }
        }
    }

    private fun findTool(projectDir: File, vararg names: String): String {
        for (name in names) {
            val local = File(projectDir, name)
            if (local.exists()) return local.absolutePath
            val path = findOnPath(name)
            if (path != null) return path
        }
        throw RuntimeException("Tool not found: ${names.joinToString()}")
    }

    private fun findTool(name: String): String? = findOnPath(name)

    private fun findOnPath(name: String): String? {
        val pathEnv = System.getenv("PATH") ?: return null
        for (dir in pathEnv.split(File.pathSeparator)) {
            val f = File(dir, name)
            if (f.exists()) return f.absolutePath
            if (name.indexOf('.') == -1) {
                val fWindows = File(dir, "$name.exe")
                if (fWindows.exists()) return fWindows.absolutePath
            }
        }
        return null
    }

    private fun findSources(projectDir: File): String {
        val src = File(projectDir, "src")
        if (!src.isDirectory) return projectDir.absolutePath
        val sb = StringBuilder()
        try {
            java.nio.file.Files.walk(src.toPath())
                .filter { it.toString().endsWith(".java") }
                .limit(1)
                .forEach {
                    if (sb.isNotEmpty()) sb.append(File.pathSeparator)
                    sb.append(it.parent.toString())
                }
        } catch (e: Exception) {
            LOGGER.fine("Error scanning sources: $e")
        }
        return if (sb.isNotEmpty()) sb.toString() else projectDir.absolutePath
    }
}
