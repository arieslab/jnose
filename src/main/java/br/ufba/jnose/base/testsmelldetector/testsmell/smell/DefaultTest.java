package br.ufba.jnose.base.testsmelldetector.testsmell.smell;

import br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest extends br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell {

    private ArrayList<br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage> instanceDefault;
    public DefaultTest() {
        super("Default Test");
        instanceDefault = new ArrayList<> (  );
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new DefaultTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        for (br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage method : instanceDefault) {
            br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod testClass = new TestMethod(method.getTestMethodName());
            testClass.addDataItem("begin", method.getBlock());
            testClass.addDataItem("end", method.getBlock()); // [Remover]
            testClass.setHasSmell(true);
            smellyElementList.add(testClass);
        }
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<br.ufba.jnose.base.testsmelldetector.testsmell.SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getNameAsString().equals("ExampleUnitTest") || n.getNameAsString().equals("ExampleInstrumentedTest")) {
                instanceDefault.add(new br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage(n.getNameAsString(), "",
                        String.valueOf(n.getRange().get().begin.line),
                        String.valueOf(n.getRange().get().end.line)));
            }
            super.visit(n, arg);
        }
    }
}
