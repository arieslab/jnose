package br.ufba.jnose.pages;

import br.ufba.jnose.core.testsmelldetector.testsmell.TestSmellDetector;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
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

    public ConfigPage() {

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

        Form form = new Form<String>("form"){
            @Override
            protected void onSubmit() {
                TestSmellDetector.emptyTest = emptyTest;
                TestSmellDetector.exceptionCatchingThrowing = exceptionCatchingThrowing;
                TestSmellDetector.generalFixture = generalFixture;
                TestSmellDetector.mysteryGuest = mysteryGuest;
                TestSmellDetector.sleepyTest = sleepyTest;
                TestSmellDetector.verboseTest = verboseTest;
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
            }
        };

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
        form.add(new CheckBox("cbVerboseTest", new PropertyModel<>(this, "verboseTest")));
        form.add(new CheckBox("cbSensitiveEquality", new PropertyModel<>(this, "sensitiveEquality")));
        form.add(new CheckBox("cbRedundantAssertion", new PropertyModel<>(this, "redundantAssertion")));
        form.add(new CheckBox("cbPrintStatement", new PropertyModel<>(this, "printStatement")));
        form.add(new CheckBox("cbMagicNumberTest", new PropertyModel<>(this, "magicNumberTest")));
        form.add(new CheckBox("cbResourceOptimism", new PropertyModel<>(this, "resourceOptimism")));
        form.add(new CheckBox("cbIgnoredTest", new PropertyModel<>(this, "ignoredTest")));
        form.add(new CheckBox("cbUnknownTest", new PropertyModel<>(this, "unknownTest")));
        form.add(new CheckBox("cbLazyTest", new PropertyModel<>(this, "lazyTest")));

        add(form);

    }
}