package io.github.arieslab.base.cobertura

import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.logging.Logger

object CoverageClasspathResolver {

    private val LOGGER = Logger.getLogger(CoverageClasspathResolver::class.java.name)
    private val MAVEN_REPO = "${System.getProperty("user.home")}/.m2/repository"

    fun resolve(projectDir: File): String {
        return resolve(projectDir, BuildExecutor.detect(projectDir))
    }

    fun resolve(projectDir: File, type: BuildType): String {
        val entries = LinkedHashSet<String>()

        addIfExists(entries, type.getClassesDir(projectDir))
        addIfExists(entries, type.getTestClassesDir(projectDir))

        when (type) {
            BuildType.MAVEN -> resolveMavenDeps(projectDir, entries)
            BuildType.GRADLE -> {
                addIfExists(entries, projectDir, "build/libs")
                scanDir(entries, projectDir, "libs")
            }
            BuildType.ANT -> scanDir(entries, projectDir, "lib")
            else -> {}
        }

        scanDir(entries, projectDir, "lib")
        scanDir(entries, projectDir, "libs")
        scanDir(entries, projectDir, "WEB-INF/lib")
        scanDir(entries, projectDir, "WEB-INF/classes")

        return entries.joinToString(File.pathSeparator)
    }

    private fun resolveMavenDeps(projectDir: File, entries: MutableSet<String>) {
        val visited = HashSet<String>()
        try {
            resolvePomRecursive(File(projectDir, "pom.xml"), entries, visited)
        } catch (e: Exception) {
            LOGGER.warning("Failed to resolve Maven dependencies from $projectDir: $e")
        }
    }

    private fun resolvePomRecursive(pomFile: File?, entries: MutableSet<String>, visited: MutableSet<String>) {
        if (pomFile == null || !pomFile.exists()) return

        try {
            Files.newBufferedReader(pomFile.toPath(), StandardCharsets.UTF_8).use { reader ->
                val model = MavenXpp3Reader().read(reader)
                val props = collectProperties(model)

                val parent = model.parent
                if (parent != null) {
                    val parentPom = findMavenArtifact(
                        parent.groupId,
                        parent.artifactId,
                        resolveVersion(parent.version, props) ?: "")
                    if (parentPom != null) {
                        val parentDir = parentPom.parentFile
                        addIfExists(entries, parentDir.parentFile, "${parentDir.name}.jar")
                    }
                }

                for (dep in model.dependencies) {
                    val key = "${dep.groupId}:${dep.artifactId}"
                    if (visited.contains(key)) continue
                    visited.add(key)

                    val version = resolveVersion(dep.version, props)
                    if (version == null || version.isEmpty() || version.contains("\${")) continue

                    val jar = findMavenArtifact(dep.groupId, dep.artifactId, version)
                    if (jar != null) {
                        entries.add(jar.absolutePath)
                    }

                    val depPom = findMavenArtifact(dep.groupId, dep.artifactId, version, "pom")
                    if (depPom != null) {
                        resolvePomRecursive(depPom, entries, visited)
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.fine("Skipping POM: $pomFile - $e")
        }
    }

    private fun collectProperties(model: org.apache.maven.model.Model): Properties {
        val props = Properties()
        if (model.parent != null) {
            val parentPom = findMavenArtifact(
                model.parent.groupId,
                model.parent.artifactId,
                model.parent.version ?: "")
            if (parentPom != null) {
                try {
                    Files.newBufferedReader(parentPom.toPath(), StandardCharsets.UTF_8).use { r ->
                        val parentModel = MavenXpp3Reader().read(r)
                        props.putAll(parentModel.properties)
                    }
                } catch (_: Exception) {}
            }
        }
        props.putAll(model.properties)
        props["project.version"] = model.version ?: ""
        props["project.groupId"] = model.groupId
        return props
    }

    private fun resolveVersion(version: String?, props: Properties): String? {
        if (version == null) return null
        var resolved: String = version
        while (resolved.contains("\${")) {
            val start = resolved.indexOf("\${")
            val end = resolved.indexOf("}", start)
            if (end == -1) break
            val key = resolved.substring(start + 2, end)
            val value = props.getProperty(key) ?: break
            resolved = resolved.substring(0, start) + value + resolved.substring(end + 1)
        }
        return resolved
    }

    private fun findMavenArtifact(groupId: String, artifactId: String, version: String): File? {
        return findMavenArtifact(groupId, artifactId, version, "jar")
    }

    private fun findMavenArtifact(groupId: String, artifactId: String, version: String, type: String): File? {
        val path = "$MAVEN_REPO/${groupId.replace('.', '/')}/$artifactId/$version/$artifactId-$version.$type"
        val file = File(path)
        return if (file.exists()) file else null
    }

    private fun addIfExists(entries: MutableSet<String>, f: File) {
        if (f.exists()) entries.add(f.absolutePath)
    }

    private fun addIfExists(entries: MutableSet<String>, base: File, relative: String) {
        val f = File(base, relative)
        if (f.exists()) entries.add(f.absolutePath)
    }

    private fun scanDir(entries: MutableSet<String>, base: File, relative: String) {
        val dir = File(base, relative)
        if (!dir.isDirectory) return
        try {
            Files.walk(dir.toPath())
                .filter { it.toFile().isFile }
                .filter { it.toString().endsWith(".jar") }
                .map { it.toAbsolutePath().toString() }
                .forEach { entries.add(it) }
        } catch (e: Exception) {
            LOGGER.fine("Skipping scan: $dir - $e")
        }
    }
}
