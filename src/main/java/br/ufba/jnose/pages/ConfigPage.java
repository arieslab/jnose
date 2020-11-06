package br.ufba.jnose.pages;

import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.core.testsmelldetector.testsmell.smell.*;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

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

    public ConfigPage() {
        super("ConfigPage");

        this.verboseTestMaxStatements = VerboseTest.MAX_STATEMENTS+"";

        emptyTest = TestSmellDetector.emptyTest;
        exceptionCatchingThrowing = TestSmellDetector.exceptionCatchingThrowing;
        generalFixture = TestSmellDetector.generalFixture;
        mysteryGuest = TestSmellDetector.mysteryGuest;
        sleepyTest = TestSmellDetector.sleepyTest;
        verboseTest = TestSmellDetector.verboseTest;
        sensitiveEquality = TestSmellDetector.sensitiveEquality;
        redundantAssertion = TestSmellDetector.redundantAssertion;
        printStatement = TestSmellDetector.printStatement;
        magicNumberTest = TestSmellDetector.magicNumberTest;
        resourceOptimism = TestSmellDetector.resourceOptimism;
        ignoredTest = TestSmellDetector.ignoredTest;
        unknownTest = TestSmellDetector.unknownTest;
        lazyTest = TestSmellDetector.lazyTest;
        assertionRoulette = TestSmellDetector.assertionRoulette;
        conditionalTestLogic = TestSmellDetector.conditionalTestLogic;
        constructorInitialization = TestSmellDetector.constructorInitialization;
        defaultTest = TestSmellDetector.defaultTest;
        dependentTest = TestSmellDetector.dependentTest;
        duplicateAssert = TestSmellDetector.duplicateAssert;
        eagerTest = TestSmellDetector.eagerTest;

        Form form = new Form<String>("form") {
            @Override
            protected void onSubmit() {
                TestSmellDetector.emptyTest = emptyTest;
                TestSmellDetector.exceptionCatchingThrowing = exceptionCatchingThrowing;
                TestSmellDetector.generalFixture = generalFixture;
                TestSmellDetector.mysteryGuest = mysteryGuest;
                TestSmellDetector.sleepyTest = sleepyTest;
                TestSmellDetector.sensitiveEquality = sensitiveEquality;
                TestSmellDetector.redundantAssertion = redundantAssertion;
                TestSmellDetector.printStatement = printStatement;
                TestSmellDetector.magicNumberTest = magicNumberTest;
                TestSmellDetector.resourceOptimism = resourceOptimism;
                TestSmellDetector.ignoredTest = ignoredTest;
                TestSmellDetector.unknownTest = unknownTest;
                TestSmellDetector.lazyTest = lazyTest;
                TestSmellDetector.assertionRoulette = assertionRoulette;
                TestSmellDetector.conditionalTestLogic = conditionalTestLogic;
                TestSmellDetector.constructorInitialization = constructorInitialization;
                TestSmellDetector.defaultTest = defaultTest;
                TestSmellDetector.dependentTest = dependentTest;
                TestSmellDetector.duplicateAssert = duplicateAssert;
                TestSmellDetector.eagerTest = eagerTest;

                TestSmellDetector.verboseTest = verboseTest;
                VerboseTest.MAX_STATEMENTS = Integer.parseInt(verboseTestMaxStatements);
            }
        };

        form.add(new Button("btAll"){
            @Override
            public void onSubmit() {
                TestSmellDetector.emptyTest = true;
                TestSmellDetector.exceptionCatchingThrowing = true;
                TestSmellDetector.generalFixture = true;
                TestSmellDetector.mysteryGuest = true;
                TestSmellDetector.sleepyTest = true;
                TestSmellDetector.verboseTest = true;
                TestSmellDetector.sensitiveEquality = true;
                TestSmellDetector.redundantAssertion = true;
                TestSmellDetector.printStatement = true;
                TestSmellDetector.magicNumberTest = true;
                TestSmellDetector.resourceOptimism = true;
                TestSmellDetector.ignoredTest = true;
                TestSmellDetector.unknownTest = true;
                TestSmellDetector.lazyTest = true;
                TestSmellDetector.assertionRoulette = true;
                TestSmellDetector.conditionalTestLogic = true;
                TestSmellDetector.constructorInitialization = true;
                TestSmellDetector.defaultTest = true;
                TestSmellDetector.dependentTest = true;
                TestSmellDetector.duplicateAssert = true;
                TestSmellDetector.eagerTest = true;

                emptyTest = TestSmellDetector.emptyTest;
                exceptionCatchingThrowing = TestSmellDetector.exceptionCatchingThrowing;
                generalFixture = TestSmellDetector.generalFixture;
                mysteryGuest = TestSmellDetector.mysteryGuest;
                sleepyTest = TestSmellDetector.sleepyTest;
                verboseTest = TestSmellDetector.verboseTest;
                sensitiveEquality = TestSmellDetector.sensitiveEquality;
                redundantAssertion = TestSmellDetector.redundantAssertion;
                printStatement = TestSmellDetector.printStatement;
                magicNumberTest = TestSmellDetector.magicNumberTest;
                resourceOptimism = TestSmellDetector.resourceOptimism;
                ignoredTest = TestSmellDetector.ignoredTest;
                unknownTest = TestSmellDetector.unknownTest;
                lazyTest = TestSmellDetector.lazyTest;
                assertionRoulette = TestSmellDetector.assertionRoulette;
                conditionalTestLogic = TestSmellDetector.conditionalTestLogic;
                constructorInitialization = TestSmellDetector.constructorInitialization;
                defaultTest = TestSmellDetector.defaultTest;
                dependentTest = TestSmellDetector.dependentTest;
                duplicateAssert = TestSmellDetector.duplicateAssert;
                eagerTest = TestSmellDetector.eagerTest;

                ConfigPage.this.setResponsePage(ConfigPage.class);
            }
        });

        form.add(new Button("btDAll"){
            @Override
            public void onSubmit() {
                TestSmellDetector.emptyTest = false;
                TestSmellDetector.exceptionCatchingThrowing = false;
                TestSmellDetector.generalFixture = false;
                TestSmellDetector.mysteryGuest = false;
                TestSmellDetector.sleepyTest = false;
                TestSmellDetector.verboseTest = false;
                TestSmellDetector.sensitiveEquality = false;
                TestSmellDetector.redundantAssertion = false;
                TestSmellDetector.printStatement = false;
                TestSmellDetector.magicNumberTest = false;
                TestSmellDetector.resourceOptimism = false;
                TestSmellDetector.ignoredTest = false;
                TestSmellDetector.unknownTest = false;
                TestSmellDetector.lazyTest = false;
                TestSmellDetector.assertionRoulette = false;
                TestSmellDetector.conditionalTestLogic = false;
                TestSmellDetector.constructorInitialization = false;
                TestSmellDetector.defaultTest = false;
                TestSmellDetector.dependentTest = false;
                TestSmellDetector.duplicateAssert = false;
                TestSmellDetector.eagerTest = false;

                emptyTest = TestSmellDetector.emptyTest;
                exceptionCatchingThrowing = TestSmellDetector.exceptionCatchingThrowing;
                generalFixture = TestSmellDetector.generalFixture;
                mysteryGuest = TestSmellDetector.mysteryGuest;
                sleepyTest = TestSmellDetector.sleepyTest;
                verboseTest = TestSmellDetector.verboseTest;
                sensitiveEquality = TestSmellDetector.sensitiveEquality;
                redundantAssertion = TestSmellDetector.redundantAssertion;
                printStatement = TestSmellDetector.printStatement;
                magicNumberTest = TestSmellDetector.magicNumberTest;
                resourceOptimism = TestSmellDetector.resourceOptimism;
                ignoredTest = TestSmellDetector.ignoredTest;
                unknownTest = TestSmellDetector.unknownTest;
                lazyTest = TestSmellDetector.lazyTest;
                assertionRoulette = TestSmellDetector.assertionRoulette;
                conditionalTestLogic = TestSmellDetector.conditionalTestLogic;
                constructorInitialization = TestSmellDetector.constructorInitialization;
                defaultTest = TestSmellDetector.defaultTest;
                dependentTest = TestSmellDetector.dependentTest;
                duplicateAssert = TestSmellDetector.duplicateAssert;
                eagerTest = TestSmellDetector.eagerTest;

                ConfigPage.this.setResponsePage(ConfigPage.class);
            }
        });

        form.add(new CheckBox("cbAssertionRoulette", new PropertyModel<>(this, "assertionRoulette")));
        form.add(new CheckBox("cbConditionalTestLogic", new PropertyModel<>(this, "conditionalTestLogic")));
        form.add(new CheckBox("cbConstructorInitialization", new PropertyModel<>(this, "constructorInitialization")));
        form.add(new CheckBox("cbDefaultTest", new PropertyModel<>(this, "defaultTest")));
        form.add(new CheckBox("cbDependentTest", new PropertyModel<>(this, "dependentTest")));
        form.add(new CheckBox("cbDuplicateAssert", new PropertyModel<>(this, "duplicateAssert")));
        form.add(new CheckBox("cbEagerTest", new PropertyModel<>(this, "eagerTest")));
        form.add(new CheckBox("cbEmptyTest", new PropertyModel<>(this, "emptyTest")));
        form.add(new CheckBox("cbExceptionCatchingThrowing", new PropertyModel<>(this, "exceptionCatchingThrowing")));
        form.add(new CheckBox("cbGeneralFixture", new PropertyModel<>(this, "generalFixture")));
        form.add(new CheckBox("cbMysteryGuest", new PropertyModel<>(this, "mysteryGuest")));
        form.add(new CheckBox("cbSleepyTest", new PropertyModel<>(this, "sleepyTest")));
        form.add(new CheckBox("cbSensitiveEquality", new PropertyModel<>(this, "sensitiveEquality")));
        form.add(new CheckBox("cbRedundantAssertion", new PropertyModel<>(this, "redundantAssertion")));
        form.add(new CheckBox("cbPrintStatement", new PropertyModel<>(this, "printStatement")));
        form.add(new CheckBox("cbMagicNumberTest", new PropertyModel<>(this, "magicNumberTest")));
        form.add(new CheckBox("cbResourceOptimism", new PropertyModel<>(this, "resourceOptimism")));
        form.add(new CheckBox("cbIgnoredTest", new PropertyModel<>(this, "ignoredTest")));
        form.add(new CheckBox("cbUnknownTest", new PropertyModel<>(this, "unknownTest")));
        form.add(new CheckBox("cbLazyTest", new PropertyModel<>(this, "lazyTest")));

        form.add(new CheckBox("cbVerboseTest", new PropertyModel<>(this, "verboseTest")));
        form.add(new TextField<String>("verboseTestMaxStatements", new PropertyModel<>(this, "verboseTestMaxStatements")));


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