package br.ufba.jnose.base.testsmelldetector.testsmell.smell;

import br.ufba.jnose.base.testsmelldetector.testsmell.MethodUsage;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import br.ufba.jnose.base.testsmelldetector.testsmell.AbstractSmell;
import br.ufba.jnose.base.testsmelldetector.testsmell.TestMethod;
import br.ufba.jnose.base.testsmelldetector.testsmell.Util;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class LazyTest extends AbstractSmell {
    private static final String TEST_FILE = "Test";
    private static final String PRODUCTION_FILE = "Production";
    private String productionClassName;
    private List<MethodUsage> calledProductionMethods;
    private List<MethodDeclaration> productionMethods;
    private ArrayList<MethodUsage> instanceLazy;

    public LazyTest() {
        super("Lazy Test");
        productionMethods = new ArrayList<>();
        calledProductionMethods = new ArrayList<>();
        instanceLazy = new ArrayList<>();
    }

    /**
     * Analyze the test file for test methods that exhibit the 'Lazy Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {

        if (productionFileCompilationUnit == null)
            throw new FileNotFoundException();

        classVisitor = new LazyTest.ClassVisitor(PRODUCTION_FILE);
        classVisitor.visit(productionFileCompilationUnit, null);

        classVisitor = new LazyTest.ClassVisitor(TEST_FILE);
        classVisitor.visit(testFileCompilationUnit, null);

        ArrayList<String> productionChecked = new ArrayList<> ();

        for (MethodUsage method : calledProductionMethods) {
            ArrayList<String> range = new ArrayList<> (  );
            ArrayList<String> methodsList = new ArrayList<>();
            List<MethodUsage> s = calledProductionMethods.stream().filter(x -> x.getProductionMethodName().equals(method.getProductionMethodName())).collect(Collectors.toList());
            for(MethodUsage teste : s){
                range.add (teste.getLine());
                if(!methodsList.contains(teste.getTestMethodName())) {
                    methodsList.add ( teste.getTestMethodName ( ) );
                }
            }
            if(!productionChecked.contains(method.getProductionMethodName ())){
                productionChecked.add (method.getProductionMethodName ());
                instanceLazy.add (new MethodUsage(String.join(", ", methodsList), range));
            }
        }

        for (MethodUsage method : instanceLazy) {
            TestMethod testClass = new TestMethod(method.getTestMethodName());
            testClass.addDataItem("begin", method.getRange());
            testClass.addDataItem("end", method.getRange()); // [Remover]
            testClass.setHasSmell(true);
            smellyElementList.add(testClass);
        }
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private List<String> productionVariables = new ArrayList<>();
        private String fileType;

        public ClassVisitor(String type) {
            fileType = type;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (Objects.equals(fileType, PRODUCTION_FILE)) {
                productionClassName = n.getNameAsString();
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(EnumDeclaration n, Void arg) {
            if (Objects.equals(fileType, PRODUCTION_FILE)) {
                productionClassName = n.getNameAsString();
            }
            super.visit(n, arg);
        }

        /**
         * The purpose of this method is to 'visit' all test methods.
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            // ensure that this method is only executed for the test file
            if (Objects.equals(fileType, TEST_FILE)) {
                if (Util.isValidTestMethod(n)) {
                    currentMethod = n;
                    testMethod = new TestMethod(currentMethod.getNameAsString());
                    testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                    super.visit(n, arg);

                    //reset values for next method
                    currentMethod = null;
                    productionVariables = new ArrayList<>();
                }
            } else { //collect a list of all public/protected members of the production class
                for (Modifier modifier : n.getModifiers()) {
                    if (modifier.name().toLowerCase().equals("public") || modifier.name().toLowerCase().equals("protected")) {
                        productionMethods.add(n);
                    }
                }
            }
        }


        /**
         * The purpose of this method is to identify the production class methods that are called from the test method
         * When the parser encounters a method call:
         * 1) the method is contained in the productionMethods list
         * or
         * 2) the code will check the 'scope' of the called method
         * A match is made if the scope is either:
         * equal to the name of the production class (as in the case of a static method) or
         * if the scope is a variable that has been declared to be of type of the production class (i.e. contained in the 'productionVariables' list).
         */
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                if (productionMethods.stream().anyMatch(i -> i.getNameAsString().equals(n.getNameAsString()) &&
                        i.getParameters().size() == n.getArguments().size())) {
                    calledProductionMethods.add(new MethodUsage(currentMethod.getNameAsString(), n.getNameAsString(),
                            String.valueOf(n.getRange().get().begin.line), ""));
                    } else {
                    if (n.getScope().isPresent()) {
                        if (n.getScope().get() instanceof NameExpr) {
                            //checks if the scope of the method being called is either of production class (e.g. static method)
                            //or
                            ///if the scope matches a variable which, in turn, is of type of the production class
                            if (((NameExpr) n.getScope().get()).getNameAsString().equals(productionClassName) ||
                                    productionVariables.contains(((NameExpr) n.getScope().get()).getNameAsString())) {
                                calledProductionMethods.add(new MethodUsage(currentMethod.getNameAsString(), n.getNameAsString(),
                                        String.valueOf(n.getRange().get().begin.line), ""));

                            }
                        }
                    }
                }
            }
        }

//        /**
//         * The purpose of this method is to capture the names of all variables, declared in the method body, that are of type of the production class.
//         * The variable is captured as and when the code statement is parsed/evaluated by the parser
//         */
//        @Override
//        public void visit(VariableDeclarationExpr n, Void arg) {
//            if (currentMethod != null) {
//                for (int i = 0; i < n.getVariables().size(); i++) {
//                    if (productionClassName.equals(n.getVariable(i).getType().asString())) {
//                        productionVariables.add(n.getVariable(i).getNameAsString());
//                    }
//                }
//            }
//            super.visit(n, arg);
//        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            if (Objects.equals(fileType, TEST_FILE)) {
                if (productionClassName.equals(n.getType().asString())) {
                    productionVariables.add(n.getNameAsString());
                }
            }
            super.visit(n, arg);
        }
    }
}
