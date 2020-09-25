package br.ufba.jnose.core.testsmelldetector.testsmell;

import br.ufba.jnose.core.testsmelldetector.testsmell.smell.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestSmellDetector {

    private List<AbstractSmell> testSmells;

    public static Boolean assertionRoulette = true;
    public static Boolean conditionalTestLogic = true;
    public static Boolean constructorInitialization = true;
    public static Boolean defaultTest = true;
    public static Boolean dependentTest = true;
    public static Boolean duplicateAssert = true;
    public static Boolean eagerTest = true;
    public static Boolean emptyTest = true;
    public static Boolean exceptionCatchingThrowing = true;
    public static Boolean generalFixture = true;
    public static Boolean mysteryGuest = true;
    public static Boolean printStatement = true;
    public static Boolean redundantAssertion = true;
    public static Boolean sensitiveEquality = true;
    public static Boolean verboseTest = true;
    public static Boolean sleepyTest = true;
    public static Boolean lazyTest = true;
    public static Boolean unknownTest = true;
    public static Boolean ignoredTest = true;
    public static Boolean resourceOptimism = true;
    public static Boolean magicNumberTest = true;

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an List
     */
    public TestSmellDetector() {
        initializeSmells();
    }

    private void initializeSmells(){
        testSmells = new ArrayList<>();
        if(unknownTest) testSmells.add(new UnknownTest());
        if(ignoredTest) testSmells.add(new IgnoredTest());
        if(resourceOptimism) testSmells.add(new ResourceOptimism());
        if(magicNumberTest) testSmells.add(new MagicNumberTest());
        if(redundantAssertion) testSmells.add(new RedundantAssertion());
        if(sensitiveEquality) testSmells.add(new SensitiveEquality());
        if(verboseTest) testSmells.add(new VerboseTest());
        if(sleepyTest) testSmells.add(new SleepyTest());
        if(lazyTest) testSmells.add(new LazyTest());
        if(duplicateAssert) testSmells.add(new DuplicateAssert());
        if(eagerTest) testSmells.add(new EagerTest());
        if(assertionRoulette) testSmells.add(new AssertionRoulette());
        if(conditionalTestLogic) testSmells.add(new ConditionalTestLogic());
        if(constructorInitialization) testSmells.add(new ConstructorInitialization());
        if(defaultTest) testSmells.add(new DefaultTest());
        if(emptyTest) testSmells.add(new EmptyTest());
        if(exceptionCatchingThrowing) testSmells.add(new ExceptionCatchingThrowing());
        if(generalFixture) testSmells.add(new GeneralFixture());
        if(mysteryGuest) testSmells.add(new MysteryGuest());
        if(printStatement) testSmells.add(new PrintStatement());
        if(dependentTest)testSmells.add(new DependentTest());
    }

    /**
     * Factory method that provides a new instance of the TestSmellDetector
     *
     * @return new TestSmellDetector instance
     */
    public static TestSmellDetector createTestSmellDetector() {
        return new TestSmellDetector();
    }

    /**
     * Provides the names of the smells that are being checked for in the code
     *
     * @return list of smell names
     */
    public List<String> getTestSmellNames() {
        return testSmells.stream().map(AbstractSmell::getSmellName).collect(Collectors.toList());
    }

    /**
     * Loads the java source code file into an AST and then analyzes it for the existence of the different types of test smells
     */
    public TestFile detectSmells(TestFile testFile) throws IOException {
        CompilationUnit testFileCompilationUnit=null, productionFileCompilationUnit=null;
        FileInputStream testFileInputStream, productionFileInputStream;

        if(!testFile.getTestFilePath().isEmpty()) {
            testFileInputStream = new FileInputStream(testFile.getTestFilePath());
            //Linha que dar problema de memoria.
            testFileCompilationUnit = JavaParser.parse(testFileInputStream);
        }

        if(!testFile.getProductionFilePath().isEmpty()){
            productionFileInputStream = new FileInputStream(testFile.getProductionFilePath());
            productionFileCompilationUnit = JavaParser.parse(productionFileInputStream);
        }

        initializeSmells();
        for (AbstractSmell smell : testSmells) {
            try {
                smell.runAnalysis(testFileCompilationUnit, productionFileCompilationUnit,testFile.getTestFileNameWithoutExtension(),testFile.getProductionFileNameWithoutExtension());
            } catch (FileNotFoundException e) {
                testFile.addSmell(null);
                continue;
            }
            testFile.addSmell(smell);
        }

        return testFile;
    }

}
