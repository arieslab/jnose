package br.ufba.jnose.base.testsmelldetector.testsmell.smell;

import br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/*
If a test methods contains a statements that exceeds a certain threshold, the method is marked as smelly
 */
public class VerboseTest extends br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell {

    private ArrayList<br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage> instanceAbstract;
    public static int MAX_STATEMENTS = 10;

    public VerboseTest() {
        super("Verbose Test");
        instanceAbstract = new ArrayList<> (  );
    }

    /**
     * Analyze the test file for test methods for the 'Verbose Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new VerboseTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        for (br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage method : instanceAbstract) {
            br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod testClass = new TestMethod(method.getTestMethodName());
            testClass.addDataItem("begin", method.getBlock ());
            testClass.addDataItem("end", method.getBlock ()); // [Remover]
            testClass.setHasSmell(true);
            smellyElementList.add(testClass);
        }
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
//        final int MAX_STATEMENTS = 123;
        private MethodDeclaration currentMethod = null;
        private int verboseCount = 0;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (br.ufba.jnose.base.testsmelldetector.testsmell.Util.isValidTestMethod(n)) {
                currentMethod = n;

                //method should not be abstract
                if (!currentMethod.isAbstract()) {
                    if (currentMethod.getBody().isPresent()) {
                        //get the total number of statements contained in the method
                        if (currentMethod.getBody().get().getStatements().size() >= MAX_STATEMENTS) {
                            verboseCount++;
                            instanceAbstract.add ( new br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage(n.getNameAsString(), "",
                                    String.valueOf(n.getRange().get().begin.line),
                                    String.valueOf(n.getRange().get().end.line)));
                        }
                    }
                }

                //reset values for next method
                currentMethod = null;
                verboseCount = 0;
            }
        }
    }
}
