package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.SmellyElement;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestClass;

import java.io.FileNotFoundException;
import java.util.List;

/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest extends AbstractSmell {

    public DefaultTest() {
        super("Default Test");
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new DefaultTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestClass testClass;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getNameAsString().equals("ExampleUnitTest") || n.getNameAsString().equals("ExampleInstrumentedTest")) {
                testClass = new TestClass(n.getNameAsString());
                testClass.setHasSmell(true);

                testClass.addDataItem("begin",String.valueOf(n.getRange().get().begin.line));
                testClass.addDataItem("end",String.valueOf(n.getRange().get().end.line));

                smellyElementList.add(testClass);
            }
            super.visit(n, arg);
        }
    }
}
