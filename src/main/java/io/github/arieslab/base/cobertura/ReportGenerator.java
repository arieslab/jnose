package io.github.arieslab.base.cobertura;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.csv.CSVFormatter;

public class ReportGenerator {

	private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class.getName());

	private final String title;

	private File executionDataFile;
	private File classesDirectory;
	private File sourceDirectory;
	private File reportDirectory;

	private ExecFileLoader execFileLoader;

	/**
	 * Creates a new ReportGenerator for the given project directory with the specified coverage report output directory.
	 *
	 * @param projectDirectory the project root directory
	 * @param coveragereport the directory where the coverage report will be written
	 */
	public ReportGenerator(final File projectDirectory, final File coveragereport) {
		this.title = projectDirectory.getName();
		this.executionDataFile = new File(projectDirectory, "target/jacoco.exec");
		this.classesDirectory = new File(projectDirectory, "target/generated-classes");

		if(!this.classesDirectory.exists()){
			this.classesDirectory = new File(projectDirectory, "target/classes");
		}

		this.sourceDirectory = new File(projectDirectory, "src/main/java");
		this.reportDirectory = new File(coveragereport, "");
	}

	/**
	 * Loads execution data, analyzes the structure, and creates the CSV report.
	 */
	public void create() throws IOException {
		loadExecutionData();
		final IBundleCoverage bundleCoverage = analyzeStructure();
		createReport(bundleCoverage);
	}

	/**
	 * Generates a CSV coverage report from the analyzed bundle structure.
	 *
	 * @param bundleCoverage the coverage data bundle
	 */
	private void createReport(final IBundleCoverage bundleCoverage)
			throws IOException {
		final CSVFormatter csvFormatter = new CSVFormatter();
		reportDirectory.mkdir();
		IReportVisitor visitor = csvFormatter.createVisitor(new FileOutputStream(new File(reportDirectory, this.title+"_jacoco.csv")));

		visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
				execFileLoader.getExecutionDataStore().getContents());

		visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(
				sourceDirectory, "utf-8", 4));

		visitor.visitEnd();
	}

	/**
	 * Loads JaCoCo execution data from the jacoco.exec file.
	 */
	private void loadExecutionData() throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	/**
	 * Analyzes all compiled classes to build a coverage bundle.
	 *
	 * @return the bundle coverage data
	 */
	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);

		try {
			analyzer.analyzeAll(classesDirectory);
		}catch (Exception e){
			LOGGER.log(Level.WARNING, "Failed to analyze classes directory", e);
		}

		return coverageBuilder.getBundle(title);
	}


}
