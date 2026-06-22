package io.github.arieslab.pages;

import io.github.arieslab.base.TestSmellDetectorConfig;
import io.github.arieslab.pages.base.BasePage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LambdaModel;
import io.github.arieslab.core.testsmelldetector.testsmell.smell.*;

/**
 * Configuration page for enabling/disabling individual test smell detectors
 * and viewing their source code.
 */
public class ConfigPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public Boolean assertionRoulette;
    public Boolean conditionalTestLogic;
    public Boolean constructorInitialization;
    public Boolean defaultTest;
    public Boolean dependentTest;
    public Boolean duplicateAssert;
    public Boolean eagerTest;
    public Boolean lazyTest;
    public Boolean unknownTest;
    public Boolean ignoredTest;
    public Boolean resourceOptimism;
    public Boolean magicNumberTest;
    public Boolean printStatement;
    public Boolean redundantAssertion;
    public Boolean sensitiveEquality;
    public Boolean verboseTest;
    public Boolean sleepyTest;
    public Boolean emptyTest;
    public Boolean exceptionCatchingThrowing;
    public Boolean generalFixture;
    public Boolean mysteryGuest;

    public String verboseTestMaxStatements;

    /**
     * Constructs the configuration page with checkboxes for each test smell detector.
     */
    public ConfigPage() {
        super("ConfigPage");

        this.verboseTestMaxStatements = io.github.arieslab.core.testsmelldetector.testsmell.smell.VerboseTest.MAX_STATEMENTS+"";

        emptyTest = TestSmellDetectorConfig.emptyTest;
        exceptionCatchingThrowing = TestSmellDetectorConfig.exceptionCatchingThrowing;
        generalFixture = TestSmellDetectorConfig.generalFixture;
        mysteryGuest = TestSmellDetectorConfig.mysteryGuest;
        sleepyTest = TestSmellDetectorConfig.sleepyTest;
        verboseTest = TestSmellDetectorConfig.verboseTest;
        sensitiveEquality = TestSmellDetectorConfig.sensitiveEquality;
        redundantAssertion = TestSmellDetectorConfig.redundantAssertion;
        printStatement = TestSmellDetectorConfig.printStatement;
        magicNumberTest = TestSmellDetectorConfig.magicNumberTest;
        resourceOptimism = TestSmellDetectorConfig.resourceOptimism;
        ignoredTest = TestSmellDetectorConfig.ignoredTest;
        unknownTest = TestSmellDetectorConfig.unknownTest;
        lazyTest = TestSmellDetectorConfig.lazyTest;
        assertionRoulette = TestSmellDetectorConfig.assertionRoulette;
        conditionalTestLogic = TestSmellDetectorConfig.conditionalTestLogic;
        constructorInitialization = TestSmellDetectorConfig.constructorInitialization;
        defaultTest = TestSmellDetectorConfig.defaultTest;
        dependentTest = TestSmellDetectorConfig.dependentTest;
        duplicateAssert = TestSmellDetectorConfig.duplicateAssert;
        eagerTest = TestSmellDetectorConfig.eagerTest;

        Form form = new Form<String>("form") {
            @Override
            protected void onSubmit() {
                TestSmellDetectorConfig.emptyTest = emptyTest;
                TestSmellDetectorConfig.exceptionCatchingThrowing = exceptionCatchingThrowing;
                TestSmellDetectorConfig.generalFixture = generalFixture;
                TestSmellDetectorConfig.mysteryGuest = mysteryGuest;
                TestSmellDetectorConfig.sleepyTest = sleepyTest;
                TestSmellDetectorConfig.sensitiveEquality = sensitiveEquality;
                TestSmellDetectorConfig.redundantAssertion = redundantAssertion;
                TestSmellDetectorConfig.printStatement = printStatement;
                TestSmellDetectorConfig.magicNumberTest = magicNumberTest;
                TestSmellDetectorConfig.resourceOptimism = resourceOptimism;
                TestSmellDetectorConfig.ignoredTest = ignoredTest;
                TestSmellDetectorConfig.unknownTest = unknownTest;
                TestSmellDetectorConfig.lazyTest = lazyTest;
                TestSmellDetectorConfig.assertionRoulette = assertionRoulette;
                TestSmellDetectorConfig.conditionalTestLogic = conditionalTestLogic;
                TestSmellDetectorConfig.constructorInitialization = constructorInitialization;
                TestSmellDetectorConfig.defaultTest = defaultTest;
                TestSmellDetectorConfig.dependentTest = dependentTest;
                TestSmellDetectorConfig.duplicateAssert = duplicateAssert;
                TestSmellDetectorConfig.eagerTest = eagerTest;

                TestSmellDetectorConfig.verboseTest = verboseTest;
                io.github.arieslab.core.testsmelldetector.testsmell.smell.VerboseTest.MAX_STATEMENTS = Integer.parseInt(verboseTestMaxStatements);
            }
        };

        form.add(new Button("btAll"){
            @Override
            public void onSubmit() {
                TestSmellDetectorConfig.emptyTest = true;
                TestSmellDetectorConfig.exceptionCatchingThrowing = true;
                TestSmellDetectorConfig.generalFixture = true;
                TestSmellDetectorConfig.mysteryGuest = true;
                TestSmellDetectorConfig.sleepyTest = true;
                TestSmellDetectorConfig.verboseTest = true;
                TestSmellDetectorConfig.sensitiveEquality = true;
                TestSmellDetectorConfig.redundantAssertion = true;
                TestSmellDetectorConfig.printStatement = true;
                TestSmellDetectorConfig.magicNumberTest = true;
                TestSmellDetectorConfig.resourceOptimism = true;
                TestSmellDetectorConfig.ignoredTest = true;
                TestSmellDetectorConfig.unknownTest = true;
                TestSmellDetectorConfig.lazyTest = true;
                TestSmellDetectorConfig.assertionRoulette = true;
                TestSmellDetectorConfig.conditionalTestLogic = true;
                TestSmellDetectorConfig.constructorInitialization = true;
                TestSmellDetectorConfig.defaultTest = true;
                TestSmellDetectorConfig.dependentTest = true;
                TestSmellDetectorConfig.duplicateAssert = true;
                TestSmellDetectorConfig.eagerTest = true;

                emptyTest = TestSmellDetectorConfig.emptyTest;
                exceptionCatchingThrowing = TestSmellDetectorConfig.exceptionCatchingThrowing;
                generalFixture = TestSmellDetectorConfig.generalFixture;
                mysteryGuest = TestSmellDetectorConfig.mysteryGuest;
                sleepyTest = TestSmellDetectorConfig.sleepyTest;
                verboseTest = TestSmellDetectorConfig.verboseTest;
                sensitiveEquality = TestSmellDetectorConfig.sensitiveEquality;
                redundantAssertion = TestSmellDetectorConfig.redundantAssertion;
                printStatement = TestSmellDetectorConfig.printStatement;
                magicNumberTest = TestSmellDetectorConfig.magicNumberTest;
                resourceOptimism = TestSmellDetectorConfig.resourceOptimism;
                ignoredTest = TestSmellDetectorConfig.ignoredTest;
                unknownTest = TestSmellDetectorConfig.unknownTest;
                lazyTest = TestSmellDetectorConfig.lazyTest;
                assertionRoulette = TestSmellDetectorConfig.assertionRoulette;
                conditionalTestLogic = TestSmellDetectorConfig.conditionalTestLogic;
                constructorInitialization = TestSmellDetectorConfig.constructorInitialization;
                defaultTest = TestSmellDetectorConfig.defaultTest;
                dependentTest = TestSmellDetectorConfig.dependentTest;
                duplicateAssert = TestSmellDetectorConfig.duplicateAssert;
                eagerTest = TestSmellDetectorConfig.eagerTest;

                ConfigPage.this.setResponsePage(ConfigPage.class);
            }
        });

        form.add(new Button("btDAll"){
            @Override
            public void onSubmit() {
                TestSmellDetectorConfig.emptyTest = false;
                TestSmellDetectorConfig.exceptionCatchingThrowing = false;
                TestSmellDetectorConfig.generalFixture = false;
                TestSmellDetectorConfig.mysteryGuest = false;
                TestSmellDetectorConfig.sleepyTest = false;
                TestSmellDetectorConfig.verboseTest = false;
                TestSmellDetectorConfig.sensitiveEquality = false;
                TestSmellDetectorConfig.redundantAssertion = false;
                TestSmellDetectorConfig.printStatement = false;
                TestSmellDetectorConfig.magicNumberTest = false;
                TestSmellDetectorConfig.resourceOptimism = false;
                TestSmellDetectorConfig.ignoredTest = false;
                TestSmellDetectorConfig.unknownTest = false;
                TestSmellDetectorConfig.lazyTest = false;
                TestSmellDetectorConfig.assertionRoulette = false;
                TestSmellDetectorConfig.conditionalTestLogic = false;
                TestSmellDetectorConfig.constructorInitialization = false;
                TestSmellDetectorConfig.defaultTest = false;
                TestSmellDetectorConfig.dependentTest = false;
                TestSmellDetectorConfig.duplicateAssert = false;
                TestSmellDetectorConfig.eagerTest = false;

                emptyTest = TestSmellDetectorConfig.emptyTest;
                exceptionCatchingThrowing = TestSmellDetectorConfig.exceptionCatchingThrowing;
                generalFixture = TestSmellDetectorConfig.generalFixture;
                mysteryGuest = TestSmellDetectorConfig.mysteryGuest;
                sleepyTest = TestSmellDetectorConfig.sleepyTest;
                verboseTest = TestSmellDetectorConfig.verboseTest;
                sensitiveEquality = TestSmellDetectorConfig.sensitiveEquality;
                redundantAssertion = TestSmellDetectorConfig.redundantAssertion;
                printStatement = TestSmellDetectorConfig.printStatement;
                magicNumberTest = TestSmellDetectorConfig.magicNumberTest;
                resourceOptimism = TestSmellDetectorConfig.resourceOptimism;
                ignoredTest = TestSmellDetectorConfig.ignoredTest;
                unknownTest = TestSmellDetectorConfig.unknownTest;
                lazyTest = TestSmellDetectorConfig.lazyTest;
                assertionRoulette = TestSmellDetectorConfig.assertionRoulette;
                conditionalTestLogic = TestSmellDetectorConfig.conditionalTestLogic;
                constructorInitialization = TestSmellDetectorConfig.constructorInitialization;
                defaultTest = TestSmellDetectorConfig.defaultTest;
                dependentTest = TestSmellDetectorConfig.dependentTest;
                duplicateAssert = TestSmellDetectorConfig.duplicateAssert;
                eagerTest = TestSmellDetectorConfig.eagerTest;

                ConfigPage.this.setResponsePage(ConfigPage.class);
            }
        });

        form.add(new CheckBox("cbAssertionRoulette", LambdaModel.of(() -> assertionRoulette, (v) -> assertionRoulette = v)));
        form.add(new CheckBox("cbConditionalTestLogic", LambdaModel.of(() -> conditionalTestLogic, (v) -> conditionalTestLogic = v)));
        form.add(new CheckBox("cbConstructorInitialization", LambdaModel.of(() -> constructorInitialization, (v) -> constructorInitialization = v)));
        form.add(new CheckBox("cbDefaultTest", LambdaModel.of(() -> defaultTest, (v) -> defaultTest = v)));
        form.add(new CheckBox("cbDependentTest", LambdaModel.of(() -> dependentTest, (v) -> dependentTest = v)));
        form.add(new CheckBox("cbDuplicateAssert", LambdaModel.of(() -> duplicateAssert, (v) -> duplicateAssert = v)));
        form.add(new CheckBox("cbEagerTest", LambdaModel.of(() -> eagerTest, (v) -> eagerTest = v)));
        form.add(new CheckBox("cbEmptyTest", LambdaModel.of(() -> emptyTest, (v) -> emptyTest = v)));
        form.add(new CheckBox("cbExceptionCatchingThrowing", LambdaModel.of(() -> exceptionCatchingThrowing, (v) -> exceptionCatchingThrowing = v)));
        form.add(new CheckBox("cbGeneralFixture", LambdaModel.of(() -> generalFixture, (v) -> generalFixture = v)));
        form.add(new CheckBox("cbMysteryGuest", LambdaModel.of(() -> mysteryGuest, (v) -> mysteryGuest = v)));
        form.add(new CheckBox("cbSleepyTest", LambdaModel.of(() -> sleepyTest, (v) -> sleepyTest = v)));
        form.add(new CheckBox("cbSensitiveEquality", LambdaModel.of(() -> sensitiveEquality, (v) -> sensitiveEquality = v)));
        form.add(new CheckBox("cbRedundantAssertion", LambdaModel.of(() -> redundantAssertion, (v) -> redundantAssertion = v)));
        form.add(new CheckBox("cbPrintStatement", LambdaModel.of(() -> printStatement, (v) -> printStatement = v)));
        form.add(new CheckBox("cbMagicNumberTest", LambdaModel.of(() -> magicNumberTest, (v) -> magicNumberTest = v)));
        form.add(new CheckBox("cbResourceOptimism", LambdaModel.of(() -> resourceOptimism, (v) -> resourceOptimism = v)));
        form.add(new CheckBox("cbIgnoredTest", LambdaModel.of(() -> ignoredTest, (v) -> ignoredTest = v)));
        form.add(new CheckBox("cbUnknownTest", LambdaModel.of(() -> unknownTest, (v) -> unknownTest = v)));
        form.add(new CheckBox("cbLazyTest", LambdaModel.of(() -> lazyTest, (v) -> lazyTest = v)));

        form.add(new CheckBox("cbVerboseTest", LambdaModel.of(() -> verboseTest, (v) -> verboseTest = v)));
        form.add(new TextField<String>("verboseTestMaxStatements", LambdaModel.of(() -> verboseTestMaxStatements, (v) -> verboseTestMaxStatements = v)));


        form.add(new Link<String>("lkSourceAssertionRoulette") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new AssertionRoulette()));
            }
        });
        form.add(new Link<String>("lkSourceConditionalTestLogic") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new ConditionalTestLogic()));
            }
        });
        form.add(new Link<String>("lkSourceConstructorInitialization") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new ConstructorInitialization()));
            }
        });
        form.add(new Link<String>("lkSourceDefaultTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new DefaultTest()));
            }
        });
        form.add(new Link<String>("lkSourceDependentTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new DependentTest()));
            }
        });
        form.add(new Link<String>("lkSourceDuplicateAssert") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new DuplicateAssert()));
            }
        });
        form.add(new Link<String>("lkSourceEagerTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new EagerTest()));
            }
        });
        form.add(new Link<String>("lkSourceEmptyTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new EmptyTest()));
            }
        });
        form.add(new Link<String>("lkSourceExceptionCatchingThrowing") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new ExceptionCatchingThrowing()));
            }
        });
        form.add(new Link<String>("lkSourceGeneralFixture") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new GeneralFixture()));
            }
        });
        form.add(new Link<String>("IgnoredTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new IgnoredTest()));
            }
        });
        form.add(new Link<String>("LazyTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new LazyTest()));
            }
        });
        form.add(new Link<String>("MagicNumberTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new MagicNumberTest()));
            }
        });
        form.add(new Link<String>("MysteryGuest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new MysteryGuest()));
            }
        });
        form.add(new Link<String>("PrintStatement") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new PrintStatement()));
            }
        });
        form.add(new Link<String>("RedundantAssertion") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new RedundantAssertion()));
            }
        });
        form.add(new Link<String>("ResourceOptimism") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new ResourceOptimism()));
            }
        });
        form.add(new Link<String>("SensitiveEquality") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new SensitiveEquality()));
            }
        });
        form.add(new Link<String>("SleepyTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new SleepyTest()));
            }
        });
        form.add(new Link<String>("UnknownTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new UnknownTest()));
            }
        });
        form.add(new Link<String>("VerboseTest") {
            @Override
            public void onClick() {
                setResponsePage(new SourcePage(new VerboseTest()));
            }
        });

        add(form);

    }
}
