package io.github.arieslab.base.cobertura

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.tools.ExecFileLoader
import org.jacoco.report.DirectorySourceFileLocator
import org.jacoco.report.csv.CSVFormatter
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger

class ReportGenerator(
    projectDirectory: File,
    coveragereport: File,
    type: BuildType = BuildExecutor.detect(projectDirectory)
) {
    private val LOGGER = Logger.getLogger(ReportGenerator::class.java.name)

    private val title: String = projectDirectory.name
    private val executionDataFile: File = type.getJacocoExecFile(projectDirectory)
    private var classesDirectory: File = type.getClassesDir(projectDirectory).takeIf { it.exists() }
        ?: File(projectDirectory, "target/classes")
    private var sourceDirectory: File = type.getSourceDir(projectDirectory).takeIf { it.exists() }
        ?: File(projectDirectory, "src/main/java")
    private val reportDirectory: File = File(coveragereport, "")
    private val execFileLoader = ExecFileLoader()

    @Throws(java.io.IOException::class)
    fun create() {
        loadExecutionData()
        val bundleCoverage = analyzeStructure()
        createReport(bundleCoverage)
    }

    @Throws(java.io.IOException::class)
    private fun createReport(bundleCoverage: IBundleCoverage) {
        val csvFormatter = CSVFormatter()
        reportDirectory.mkdir()
        val csvFile = File(reportDirectory, "${title}_jacoco.csv")
        val visitor = csvFormatter.createVisitor(FileOutputStream(csvFile))
        visitor.visitInfo(execFileLoader.sessionInfoStore.infos, execFileLoader.executionDataStore.contents)
        visitor.visitBundle(bundleCoverage, DirectorySourceFileLocator(sourceDirectory, "utf-8", 4))
        visitor.visitEnd()
    }

    @Throws(java.io.IOException::class)
    private fun loadExecutionData() {
        execFileLoader.load(executionDataFile)
    }

    @Throws(java.io.IOException::class)
    private fun analyzeStructure(): IBundleCoverage {
        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(execFileLoader.executionDataStore, coverageBuilder)
        try {
            analyzer.analyzeAll(classesDirectory)
        } catch (e: Exception) {
            LOGGER.warning("Failed to analyze classes directory: $e")
        }
        return coverageBuilder.getBundle(title)
    }
}
