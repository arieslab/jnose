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

    /**
     * Instantiates the various test smell analyzer classes and loads the objects into an List
     */
    public TestSmellDetector() {
        initializeSmells();
    }

    private void initializeSmells(){
        testSmells = new ArrayList<>();

        if(assertionRoulette) testSmells.add(new AssertionRoulette());
        if(conditionalTestLogic) testSmells.add(new ConditionalTestLogic());
        if(constructorInitialization) testSmells.add(new ConstructorInitialization());
        if(defaultTest) testSmells.add(new DefaultTest());
        testSmells.add(new EmptyTest());
        testSmells.add(new ExceptionCatchingThrowing());
        testSmells.add(new GeneralFixture());
        testSmells.add(new MysteryGuest());
        testSmells.add(new PrintStatement());
        testSmells.add(new RedundantAssertion());
        testSmells.add(new SensitiveEquality());
        testSmells.add(new VerboseTest());
        testSmells.add(new SleepyTest());
        if(eagerTest) testSmells.add(new EagerTest());
        testSmells.add(new LazyTest());
        if(duplicateAssert) testSmells.add(new DuplicateAssert());
        testSmells.add(new UnknownTest());
        testSmells.add(new IgnoredTest());
        testSmells.add(new ResourceOptimism());
        testSmells.add(new MagicNumberTest());
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
