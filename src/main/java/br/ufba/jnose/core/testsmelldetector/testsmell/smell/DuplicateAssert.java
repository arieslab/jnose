package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestMethod;
import br.ufba.jnose.core.testsmelldetector.testsmell.Util;

import java.io.FileNotFoundException;
import java.util.*;

public class DuplicateAssert extends AbstractSmell {

    public DuplicateAssert() {
        super("Duplicate Assert");
    }
    public class DuplicateAssertStructure {

        String text;
        int line;
        boolean checked;

        public DuplicateAssertStructure(String text,int line) {
            super();
            this.text = text;
            this.line = line;
            this.checked = false;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

    }
    /**
     * Analyze the test file for test methods that have multiple assert statements with the same explanation message
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new DuplicateAssert.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        List<String> assertMessage = new ArrayList<>();
        List<String> assertMethod = new ArrayList<>();
        List<DuplicateAssertStructure> assertMethodDA = new ArrayList<>();

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)

                testMethod.addDataItem("begin",String.valueOf(n.getRange().get().begin.line));
                testMethod.addDataItem("end",String.valueOf(n.getRange().get().end.line));

                super.visit(n, arg);

                // if there are duplicate messages, then the smell exists
                Set<String> set1 = new HashSet<String>(assertMessage);
                if (set1.size() < assertMessage.size()) {
                    testMethod.setHasSmell(true);
                }

                // if there are duplicate assert methods, then the smell exists
                Set<String> set2 = new HashSet<String>(assertMethod);
                if (set2.size() < assertMethod.size()) {
                    testMethod.setHasSmell(true);
                }

                /* *
                 * Identification of all duplicate occurrences within the method
                 * */

                List<DuplicateAssertStructure> teste = assertMethodDA;

                for (int i = 0; i < teste.size() ; i++ ) {
                    if (!teste.get(i).isChecked()) {
                        String lines = "";
                        boolean hasSmell = false;
                        for (int j = i + 1; j < teste.size() ; j++ ) {
                            //Só compara com outros asserts, caso o objeto ainda não tenha sido identificado como outro DA
                            if ((!teste.get(j).isChecked()) && (teste.get(i).text.equals(teste.get(j).text))) {
                                if (!hasSmell) {
                                    lines +=  teste.get(i).line;
                                    teste.get(i).setChecked(true);
                                }
                                lines += ", " + teste.get(j).line;
                                teste.get(j).setChecked(true);
                                hasSmell = true;
                            }
                        }
                        if (hasSmell) {
                            System.out.println("Duplicate Assert:" +  lines);
                        }
                    }
                }
                /*
                *
                * */

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                assertMessage = new ArrayList<>();
                assertMethod = new ArrayList<>();
                assertMethodDA = new ArrayList<>();
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called start with 'assert'
                // if the name of a method being called is an assertion and has 3 parameters
                if (n.getNameAsString().startsWith(("assertArrayEquals")) ||
                        n.getNameAsString().startsWith(("assertEquals")) ||
                        n.getNameAsString().startsWith(("assertNotSame")) ||
                        n.getNameAsString().startsWith(("assertSame")) ||
                        n.getNameAsString().startsWith(("assertThat"))) {
                    assertMethod.add(n.toString());
                    assertMethodDA.add(new DuplicateAssertStructure(n.toString(), n.getRange().get().begin.line));
                    // assert method contains a message
                    if (n.getArguments().size() == 3) {
                        assertMessage.add(n.getArgument(0).toString());
                    }

                }
                // if the name of a method being called is an assertion and has 2 parameters
                else if (n.getNameAsString().equals("assertFalse") ||
                        n.getNameAsString().equals("assertNotNull") ||
                        n.getNameAsString().equals("assertNull") ||
                        n.getNameAsString().equals("assertTrue")) {
                    assertMethod.add(n.toString());
                    assertMethodDA.add(new DuplicateAssertStructure(n.toString(), n.getRange().get().begin.line));
                    // assert method contains a message
                    if (n.getArguments().size() == 2) {
                        assertMessage.add(n.getArgument(0).toString());
                    }
                }
                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    assertMethod.add(n.toString());
                    assertMethodDA.add(new DuplicateAssertStructure(n.toString(), n.getRange().get().begin.line));
                    // fail method contains a message
                    if (n.getArguments().size() == 1) {
                        assertMessage.add(n.getArgument(0).toString());
                    }
                }

            }
        }

    }
}

