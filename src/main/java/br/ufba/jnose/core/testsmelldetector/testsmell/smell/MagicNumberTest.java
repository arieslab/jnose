package br.ufba.jnose.core.testsmelldetector.testsmell.smell;

import br.ufba.jnose.core.testsmelldetector.testsmell.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MagicNumberTest extends AbstractSmell{

    private ArrayList<MethodUsage> instances;

    public MagicNumberTest(){
        super("Magic Number Test");
        instances = new ArrayList<>();
    }

    /**
     * Analyze the test file for test methods that have magic numbers in as parameters in the assert methods
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException{
        classVisitor = new MagicNumberTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        for (MethodUsage method : instances) {
            TestMethod testClass = new TestMethod(method.getTestMethodName());
            testClass.addDataItem("begin", method.getLine());
            testClass.addDataItem("end", method.getLine()); // [Remover]
            testClass.setHasSmell(true);
            smellyElementList.add(testClass);
        }
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements(){
        return smellyElementList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void>{
        private MethodDeclaration currentMethod = null;
        private int magicCount = 0;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg){
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                super.visit(n, arg);

                //reset values for next method
                currentMethod = null;
                magicCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg){
            super.visit(n, arg);
            if (currentMethod != null) {
                boolean flag = false;
                // if the name of a method being called start with 'assert'
                if (n.getNameAsString().startsWith(("assert"))) {
                    // checks all arguments of the assert method
                    for (Expression argument : n.getArguments()) {
                        // if the argument is a number
                        if (Util.isNumber(argument.toString())) {
                            flag = checkNumber(n, flag);
                        }
                        // if the argument contains an ObjectCreationExpr (e.g. assertEquals(new Integer(2),...)
                        else if (argument instanceof ObjectCreationExpr) {
                            flag = checkObject(n, flag, (ObjectCreationExpr) argument);
                        }
                        // if the argument contains an MethodCallExpr (e.g. assertEquals(someMethod(2),...)
                        else if (argument instanceof MethodCallExpr) {
                            flag = checkMethodCall(n, flag, (MethodCallExpr) argument);
                        }
                        //if the assertTrue has a number or methodcall with numbers
                        else if (argument instanceof BinaryExpr) {
                            if (Util.isNumber(((BinaryExpr) argument).getLeft().toString()) ||
                                    Util.isNumber(((BinaryExpr) argument).getRight().toString())) {
                                flag = checkNumber(n, flag);
                            }
                            else if (((BinaryExpr) argument).getLeft() instanceof ObjectCreationExpr){
                                flag = checkObject(n, flag, (ObjectCreationExpr) ((BinaryExpr) argument).getLeft());
                            }
                            else if (((BinaryExpr) argument).getRight() instanceof ObjectCreationExpr) {
                                flag = checkObject(n, flag, (ObjectCreationExpr) ((BinaryExpr) argument).getRight());
                            }
                            else if (((BinaryExpr) argument).getLeft() instanceof MethodCallExpr){
                                flag = checkMethodCall(n, flag, (MethodCallExpr) ((BinaryExpr) argument).getLeft());
                            }
                            else if (((BinaryExpr) argument).getRight() instanceof ObjectCreationExpr) {
                                flag = checkMethodCall(n, flag, (MethodCallExpr) ((BinaryExpr) argument).getRight());
                            }
                        }
                    }
                }
            }
        }

        private boolean checkNumber(MethodCallExpr n, boolean flag){
            if (!flag) {
                instances.add(new MethodUsage(currentMethod.getNameAsString(), "",
                        String.valueOf(n.getRange().get().begin.line), ""));
                flag = true;
            }
            return flag;
        }

        private boolean checkMethodCall(MethodCallExpr n, boolean flag, MethodCallExpr argument){
            for (Expression objectArguments : argument.getArguments()) {
                if (Util.isNumber(objectArguments.toString())) {
                    flag = checkNumber(n, flag);
                }
            }
            return flag;
        }

        private boolean checkObject(MethodCallExpr n, boolean flag, ObjectCreationExpr argument){
            for (Expression objectArguments : argument.getArguments()) {
                if (Util.isNumber(objectArguments.toString())) {
                    flag = checkNumber(n, flag);
                }
            }
            return flag;
        }
    }
}
