package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.core.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.core.testsmelldetector.testsmell.TestMethod;
import br.ufba.jnose.core.testsmelldetector.testsmell.Util;

import java.io.FileNotFoundException;

/*
This class check a test method for the existence of loops and conditional statements in the methods body
 */
public class ConditionalTestLogic extends AbstractSmell {

    public ConditionalTestLogic() {
        super("Conditional Test Logic");
    }

    /**
     * Analyze the test file for test methods that use conditional statements
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        classVisitor = new ConditionalTestLogic.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int conditionCount, ifCount, switchCount, forCount, foreachCount, whileCount, doCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(conditionCount > 0 | ifCount > 0 | switchCount > 0 | foreachCount > 0 | forCount > 0 | whileCount > 0 | doCount > 0);

                testMethod.addDataItem("ConditionCount", String.valueOf(conditionCount));
                testMethod.addDataItem("IfCount", String.valueOf(ifCount));
                testMethod.addDataItem("SwitchCount", String.valueOf(switchCount));
                testMethod.addDataItem("ForeachCount", String.valueOf(foreachCount));
                testMethod.addDataItem("ForCount", String.valueOf(forCount));
                testMethod.addDataItem("WhileCount", String.valueOf(whileCount));
                testMethod.addDataItem("DoCount", String.valueOf(doCount));

                testMethod.addDataItem("begin",String.valueOf(n.getRange().get().begin.line));
                testMethod.addDataItem("end",String.valueOf(n.getRange().get().end.line));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                conditionCount = 0;
                ifCount = 0;
                switchCount = 0;
                forCount = 0;
                foreachCount = 0;
                whileCount = 0;
                doCount = 0;
            }
        }


        @Override
        public void visit(IfStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                ifCount++;
            }
        }

        @Override
        public void visit(SwitchStmt n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                switchCount++;
            }
        }

        @Override
        public void visit(ConditionalExpr n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                conditionCount++;
            }
        }

        @Override
        public void visit(ForStmt n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                forCount++;
            }
        }

        @Override
        public void visit(ForeachStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                foreachCount++;
            }
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                whileCount++;
            }
        }

        @Override
        public void visit(DoStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                doCount++;
            }
        }
    }

}
