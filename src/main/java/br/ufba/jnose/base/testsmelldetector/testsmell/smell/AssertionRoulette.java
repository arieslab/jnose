package br.ufba.jnose.base.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod;
import br.ufba.jnose.base.testsmelldetector.testsmell.Util;

import java.io.FileNotFoundException;

/**
 * "Guess what's wrong?" This smell comes from having a number of assertions in a test method that have no explanation.
 * If one of the assertions fails, you do not know which one it is.
 * A. van Deursen, L. Moonen, A. Bergh, G. Kok, “Refactoring Test Code”, Technical Report, CWI, 2001.
 */
public class AssertionRoulette extends AbstractSmell {

    public AssertionRoulette() {
        super("Assertion Roulette");
        classVisitor = new AssertionRoulette.ClassVisitor();
    }

    /**
     * Analyze the test file for test methods for multiple assert statements without an explanation/message
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int assertNoMessageCount = 0;
        private int assertCount = 0;
        String methodName = "";
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                methodName = n.getNameAsString();
                testMethod = new TestMethod(methodName);

                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                //reset values for next method
                currentMethod = null;
                assertCount = 0;
                assertNoMessageCount = 0;
            }
        }
        public boolean explanationIsEmpty(String str) {
            char[] ch = str.toCharArray();
            String final_string = "";

            //Removes all spaces
            for(int i = 0; i < ch.length; i++ ){
                if (ch[i] != ' ') {
                    final_string += ch[i];
                }
            }
            return final_string.equals("\"\"");
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            boolean hasMissingExplanation = false;
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called is an assertion and has 3 parameters
                if (n.getNameAsString().startsWith(("assertArrayEquals")) ||
                        n.getNameAsString().startsWith(("assertEquals")) ||
                        n.getNameAsString().startsWith(("assertNotSame")) ||
                        n.getNameAsString().startsWith(("assertSame")) ||
                        n.getNameAsString().startsWith(("assertThat"))) {
                    assertCount++;
                    // assert methods that do not contain a message
                    if (n.getArguments().size() < 3 || (explanationIsEmpty(n.getArgument(0).toString()))) {
                        assertNoMessageCount++;
                        hasMissingExplanation = true;
                    }
                }
                // if the name of a method being called is an assertion and has 2 parameters
                else if (n.getNameAsString().equals("assertFalse") ||
                        n.getNameAsString().equals("assertNotNull") ||
                        n.getNameAsString().equals("assertNull") ||
                        n.getNameAsString().equals("assertTrue")) {
                    assertCount++;
                    // assert methods that do not contain a message
                    if ((n.getArguments().size() < 2) || (explanationIsEmpty(n.getArgument(0).toString())) ) {
                        assertNoMessageCount++;
                        hasMissingExplanation = true;
                    }
                }

                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    assertCount++;
                    // fail method does not contain a message
                    if (n.getArguments().size() < 1 || (explanationIsEmpty(n.getArgument(0).toString()))) {
                        assertNoMessageCount++;
                        hasMissingExplanation = true;
                    }
                }
                if (hasMissingExplanation) {
                    testMethod = new TestMethod(methodName);
                    testMethod.getData().put("begin",n.getRange().get().begin.line+"");
                    testMethod.getData().put("end",n.getRange().get().end.line+"");

                    testMethod.setHasSmell(true);
                    testMethod.addDataItem("AssertCount", String.valueOf(assertNoMessageCount));
                    smellyElementList.add(testMethod);
                }

            }
        }
    }
}

