package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestClass;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestMethod;

import java.io.FileNotFoundException;

public class IgnoredTest extends AbstractSmell {

    private boolean flag = false;

    public IgnoredTest() {
        super("IgnoredTest");
    }

    /**
     * Analyze the test file for test methods that contain Ignored test methods
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new IgnoredTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestMethod testMethod;
        TestClass testClass;

        /**
         * This method will check if the class has the @Ignore annotation
         */
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getAnnotationByName("Ignore").isPresent()) {
                testClass = new TestClass(n.getNameAsString());
                flag = true;
            }
            super.visit(n, arg);
        }

        /**
         * The purpose of this method is to 'visit' all test methods in the test file.
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {

            //JUnit 4
            //check if test method has Ignore annotation
            if (n.getAnnotationByName("Test").isPresent()) {
                if (n.getAnnotationByName("Ignore").isPresent() || flag) {
                    testMethod = new TestMethod(n.getNameAsString());
                    testMethod.setHasSmell(true);
                    testMethod.addDataItem("begin", String.valueOf(n.getRange().get().begin.line));
                    testMethod.addDataItem("end", String.valueOf(n.getRange().get().end.line));
                    smellyElementList.add(testMethod);
                    return;
                }
            }

            //JUnit 3
            //check if test method is not public
            if (n.getNameAsString().toLowerCase().startsWith("test")) {
                if (!n.getModifiers().contains(Modifier.PUBLIC)) {
                    testMethod = new TestMethod(n.getNameAsString());
                    testMethod.setHasSmell(true);
                    testMethod.addDataItem("begin",String.valueOf(n.getRange().get().begin.line));
                    testMethod.addDataItem("end",String.valueOf(n.getRange().get().end.line));
                    smellyElementList.add(testMethod);
                    return;
                }
            }
        }

    }
}
