package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestMethod;
import br.ufba.jnose.core.testsmelldetector.testsmell.Util;

import java.io.FileNotFoundException;

/**
 * This class checks if a test method is empty (i.e. the method does not contain statements in its body)
 * If the the number of statements in the body is 0, then the method is smelly
 */
public class EmptyTest extends AbstractSmell {

    public EmptyTest() {
        super("EmptyTest");
    }

    /**
     * Analyze the test file for test methods that are empty (i.e. no method body)
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new EmptyTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestMethod testMethod;

        /**
         * The purpose of this method is to 'visit' all test methods in the test file
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)

                testMethod.addDataItem("begin",String.valueOf(n.getRange().get().begin.line));
                testMethod.addDataItem("end",String.valueOf(n.getRange().get().end.line));

                //method should not be abstract
                if (!n.isAbstract()) {
                    if (n.getBody().isPresent()) {
                        //get the total number of statements contained in the method
                        if (n.getBody().get().getStatements().size() == 0) {
                            testMethod.setHasSmell(true); //the method has no statements (i.e no body)
                        }
                    }
                }
                smellyElementList.add(testMethod);
            }
        }
    }
}
