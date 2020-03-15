package br.ufba.jnose.core.cobertura;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.xml.XMLFormatter;

public class ReportGenerator {

	private final String title;

	private File executionDataFile;
	private File classesDirectory;
	private File sourceDirectory;
	private File reportDirectory;

	private ExecFileLoader execFileLoader;

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

	public void create() throws IOException {
		loadExecutionData();
		final IBundleCoverage bundleCoverage = analyzeStructure();
		createReport(bundleCoverage);
	}

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

	private IReportVisitor createVisitor(final File outputDirectory) throws IOException {
		final List<IReportVisitor> visitors = new ArrayList<IReportVisitor>();

		outputDirectory.mkdirs();

		final XMLFormatter xmlFormatter = new XMLFormatter();
//		xmlFormatter.setOutputEncoding(outputEncoding);
		visitors.add(xmlFormatter.createVisitor(new FileOutputStream(new File(outputDirectory, "jacoco.xml"))));

		final CSVFormatter csvFormatter = new CSVFormatter();
//		csvFormatter.setOutputEncoding(outputEncoding);
		visitors.add(csvFormatter.createVisitor(new FileOutputStream(new File(outputDirectory, "jacoco.csv"))));

		final HTMLFormatter htmlFormatter = new HTMLFormatter();
//		htmlFormatter.setOutputEncoding(outputEncoding);
//		htmlFormatter.setLocale(locale);
		visitors.add(htmlFormatter.createVisitor(new FileMultiReportOutput(outputDirectory)));

		return new MultiReportVisitor(visitors);
	}

	private void loadExecutionData() throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);

		try {
			analyzer.analyzeAll(classesDirectory);
		}catch (Exception e){
			e.printStackTrace();
		}

		return coverageBuilder.getBundle(title);
	}

//	public static void main(final String[] args) throws IOException {
//		for (int i = 0; i < args.length; i++) {
//			final ReportGenerator generator = new ReportGenerator(new File(
//					args[i]));
//			generator.create();
//		}
//	}

}
