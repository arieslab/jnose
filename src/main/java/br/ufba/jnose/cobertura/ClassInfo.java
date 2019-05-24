package br.ufba.jnose.cobertura;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionDataStore;

public final class ClassInfo implements ICoverageVisitor {
    private final PrintStream out;
    private final Analyzer analyzer;

    public ClassInfo(final PrintStream out) {
        this.out = out;
        analyzer = new Analyzer(new ExecutionDataStore(), this);
    }

    public String[] execute(final String file) throws IOException {
        analyzer.analyzeAll(new File(file));
        return listaRetorno;
    }

    String[] listaRetorno = new String[7];

    public void visitCoverage(final IClassCoverage coverage) {
        listaRetorno[0] = coverage.getName();//class name
        listaRetorno[1] = coverage.getId() + "";//class id
        listaRetorno[2] = coverage.getInstructionCounter().getTotalCount() + "";//instructions
        listaRetorno[3] = coverage.getBranchCounter().getTotalCount() + "";//branches
        listaRetorno[4] = coverage.getLineCounter().getTotalCount() + "";//lines
        listaRetorno[5] = coverage.getMethodCounter().getTotalCount() + "";//methods
        listaRetorno[6] = coverage.getComplexityCounter().getTotalCount() + "";//complexity
    }

}
