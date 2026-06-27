package io.github.arieslab.pages

import io.github.arieslab.base.TestSmellDetectorConfig
import io.github.arieslab.core.testsmelldetector.testsmell.smell.*
import io.github.arieslab.pages.base.BasePage
import org.apache.wicket.markup.html.form.Button
import org.apache.wicket.markup.html.form.CheckBox
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.model.LambdaModel

class ConfigPage : BasePage("ConfigPage") {

    var assertionRoulette: Boolean = TestSmellDetectorConfig.assertionRoulette
    var conditionalTestLogic: Boolean = TestSmellDetectorConfig.conditionalTestLogic
    var constructorInitialization: Boolean = TestSmellDetectorConfig.constructorInitialization
    var defaultTest: Boolean = TestSmellDetectorConfig.defaultTest
    var dependentTest: Boolean = TestSmellDetectorConfig.dependentTest
    var duplicateAssert: Boolean = TestSmellDetectorConfig.duplicateAssert
    var eagerTest: Boolean = TestSmellDetectorConfig.eagerTest
    var lazyTest: Boolean = TestSmellDetectorConfig.lazyTest
    var unknownTest: Boolean = TestSmellDetectorConfig.unknownTest
    var ignoredTest: Boolean = TestSmellDetectorConfig.ignoredTest
    var resourceOptimism: Boolean = TestSmellDetectorConfig.resourceOptimism
    var magicNumberTest: Boolean = TestSmellDetectorConfig.magicNumberTest
    var printStatement: Boolean = TestSmellDetectorConfig.printStatement
    var redundantAssertion: Boolean = TestSmellDetectorConfig.redundantAssertion
    var sensitiveEquality: Boolean = TestSmellDetectorConfig.sensitiveEquality
    var verboseTest: Boolean = TestSmellDetectorConfig.verboseTest
    var sleepyTest: Boolean = TestSmellDetectorConfig.sleepyTest
    var emptyTest: Boolean = TestSmellDetectorConfig.emptyTest
    var exceptionCatchingThrowing: Boolean = TestSmellDetectorConfig.exceptionCatchingThrowing
    var generalFixture: Boolean = TestSmellDetectorConfig.generalFixture
    var mysteryGuest: Boolean = TestSmellDetectorConfig.mysteryGuest

    var verboseTestMaxStatements: String = VerboseTest.MAX_STATEMENTS.toString()

    init {
        val form = object : Form<String>("form") {
            override fun onSubmit() {
                TestSmellDetectorConfig.emptyTest = this@ConfigPage.emptyTest
                TestSmellDetectorConfig.exceptionCatchingThrowing = this@ConfigPage.exceptionCatchingThrowing
                TestSmellDetectorConfig.generalFixture = this@ConfigPage.generalFixture
                TestSmellDetectorConfig.mysteryGuest = this@ConfigPage.mysteryGuest
                TestSmellDetectorConfig.sleepyTest = this@ConfigPage.sleepyTest
                TestSmellDetectorConfig.sensitiveEquality = this@ConfigPage.sensitiveEquality
                TestSmellDetectorConfig.redundantAssertion = this@ConfigPage.redundantAssertion
                TestSmellDetectorConfig.printStatement = this@ConfigPage.printStatement
                TestSmellDetectorConfig.magicNumberTest = this@ConfigPage.magicNumberTest
                TestSmellDetectorConfig.resourceOptimism = this@ConfigPage.resourceOptimism
                TestSmellDetectorConfig.ignoredTest = this@ConfigPage.ignoredTest
                TestSmellDetectorConfig.unknownTest = this@ConfigPage.unknownTest
                TestSmellDetectorConfig.lazyTest = this@ConfigPage.lazyTest
                TestSmellDetectorConfig.assertionRoulette = this@ConfigPage.assertionRoulette
                TestSmellDetectorConfig.conditionalTestLogic = this@ConfigPage.conditionalTestLogic
                TestSmellDetectorConfig.constructorInitialization = this@ConfigPage.constructorInitialization
                TestSmellDetectorConfig.defaultTest = this@ConfigPage.defaultTest
                TestSmellDetectorConfig.dependentTest = this@ConfigPage.dependentTest
                TestSmellDetectorConfig.duplicateAssert = this@ConfigPage.duplicateAssert
                TestSmellDetectorConfig.eagerTest = this@ConfigPage.eagerTest
                TestSmellDetectorConfig.verboseTest = this@ConfigPage.verboseTest
                VerboseTest.MAX_STATEMENTS = this@ConfigPage.verboseTestMaxStatements.toInt()
            }
        }

        form.add(object : Button("btAll") {
            override fun onSubmit() {
                TestSmellDetectorConfig.emptyTest = true
                TestSmellDetectorConfig.exceptionCatchingThrowing = true
                TestSmellDetectorConfig.generalFixture = true
                TestSmellDetectorConfig.mysteryGuest = true
                TestSmellDetectorConfig.sleepyTest = true
                TestSmellDetectorConfig.verboseTest = true
                TestSmellDetectorConfig.sensitiveEquality = true
                TestSmellDetectorConfig.redundantAssertion = true
                TestSmellDetectorConfig.printStatement = true
                TestSmellDetectorConfig.magicNumberTest = true
                TestSmellDetectorConfig.resourceOptimism = true
                TestSmellDetectorConfig.ignoredTest = true
                TestSmellDetectorConfig.unknownTest = true
                TestSmellDetectorConfig.lazyTest = true
                TestSmellDetectorConfig.assertionRoulette = true
                TestSmellDetectorConfig.conditionalTestLogic = true
                TestSmellDetectorConfig.constructorInitialization = true
                TestSmellDetectorConfig.defaultTest = true
                TestSmellDetectorConfig.dependentTest = true
                TestSmellDetectorConfig.duplicateAssert = true
                TestSmellDetectorConfig.eagerTest = true

                emptyTest = TestSmellDetectorConfig.emptyTest
                exceptionCatchingThrowing = TestSmellDetectorConfig.exceptionCatchingThrowing
                generalFixture = TestSmellDetectorConfig.generalFixture
                mysteryGuest = TestSmellDetectorConfig.mysteryGuest
                sleepyTest = TestSmellDetectorConfig.sleepyTest
                verboseTest = TestSmellDetectorConfig.verboseTest
                sensitiveEquality = TestSmellDetectorConfig.sensitiveEquality
                redundantAssertion = TestSmellDetectorConfig.redundantAssertion
                printStatement = TestSmellDetectorConfig.printStatement
                magicNumberTest = TestSmellDetectorConfig.magicNumberTest
                resourceOptimism = TestSmellDetectorConfig.resourceOptimism
                ignoredTest = TestSmellDetectorConfig.ignoredTest
                unknownTest = TestSmellDetectorConfig.unknownTest
                lazyTest = TestSmellDetectorConfig.lazyTest
                assertionRoulette = TestSmellDetectorConfig.assertionRoulette
                conditionalTestLogic = TestSmellDetectorConfig.conditionalTestLogic
                constructorInitialization = TestSmellDetectorConfig.constructorInitialization
                defaultTest = TestSmellDetectorConfig.defaultTest
                dependentTest = TestSmellDetectorConfig.dependentTest
                duplicateAssert = TestSmellDetectorConfig.duplicateAssert
                eagerTest = TestSmellDetectorConfig.eagerTest

                setResponsePage(ConfigPage::class.java)
            }
        })

        form.add(object : Button("btDAll") {
            override fun onSubmit() {
                TestSmellDetectorConfig.emptyTest = false
                TestSmellDetectorConfig.exceptionCatchingThrowing = false
                TestSmellDetectorConfig.generalFixture = false
                TestSmellDetectorConfig.mysteryGuest = false
                TestSmellDetectorConfig.sleepyTest = false
                TestSmellDetectorConfig.verboseTest = false
                TestSmellDetectorConfig.sensitiveEquality = false
                TestSmellDetectorConfig.redundantAssertion = false
                TestSmellDetectorConfig.printStatement = false
                TestSmellDetectorConfig.magicNumberTest = false
                TestSmellDetectorConfig.resourceOptimism = false
                TestSmellDetectorConfig.ignoredTest = false
                TestSmellDetectorConfig.unknownTest = false
                TestSmellDetectorConfig.lazyTest = false
                TestSmellDetectorConfig.assertionRoulette = false
                TestSmellDetectorConfig.conditionalTestLogic = false
                TestSmellDetectorConfig.constructorInitialization = false
                TestSmellDetectorConfig.defaultTest = false
                TestSmellDetectorConfig.dependentTest = false
                TestSmellDetectorConfig.duplicateAssert = false
                TestSmellDetectorConfig.eagerTest = false

                emptyTest = TestSmellDetectorConfig.emptyTest
                exceptionCatchingThrowing = TestSmellDetectorConfig.exceptionCatchingThrowing
                generalFixture = TestSmellDetectorConfig.generalFixture
                mysteryGuest = TestSmellDetectorConfig.mysteryGuest
                sleepyTest = TestSmellDetectorConfig.sleepyTest
                verboseTest = TestSmellDetectorConfig.verboseTest
                sensitiveEquality = TestSmellDetectorConfig.sensitiveEquality
                redundantAssertion = TestSmellDetectorConfig.redundantAssertion
                printStatement = TestSmellDetectorConfig.printStatement
                magicNumberTest = TestSmellDetectorConfig.magicNumberTest
                resourceOptimism = TestSmellDetectorConfig.resourceOptimism
                ignoredTest = TestSmellDetectorConfig.ignoredTest
                unknownTest = TestSmellDetectorConfig.unknownTest
                lazyTest = TestSmellDetectorConfig.lazyTest
                assertionRoulette = TestSmellDetectorConfig.assertionRoulette
                conditionalTestLogic = TestSmellDetectorConfig.conditionalTestLogic
                constructorInitialization = TestSmellDetectorConfig.constructorInitialization
                defaultTest = TestSmellDetectorConfig.defaultTest
                dependentTest = TestSmellDetectorConfig.dependentTest
                duplicateAssert = TestSmellDetectorConfig.duplicateAssert
                eagerTest = TestSmellDetectorConfig.eagerTest

                setResponsePage(ConfigPage::class.java)
            }
        })

        form.add(CheckBox("cbAssertionRoulette", LambdaModel.of({ assertionRoulette }, { v -> assertionRoulette = v })))
        form.add(CheckBox("cbConditionalTestLogic", LambdaModel.of({ conditionalTestLogic }, { v -> conditionalTestLogic = v })))
        form.add(CheckBox("cbConstructorInitialization", LambdaModel.of({ constructorInitialization }, { v -> constructorInitialization = v })))
        form.add(CheckBox("cbDefaultTest", LambdaModel.of({ defaultTest }, { v -> defaultTest = v })))
        form.add(CheckBox("cbDependentTest", LambdaModel.of({ dependentTest }, { v -> dependentTest = v })))
        form.add(CheckBox("cbDuplicateAssert", LambdaModel.of({ duplicateAssert }, { v -> duplicateAssert = v })))
        form.add(CheckBox("cbEagerTest", LambdaModel.of({ eagerTest }, { v -> eagerTest = v })))
        form.add(CheckBox("cbEmptyTest", LambdaModel.of({ emptyTest }, { v -> emptyTest = v })))
        form.add(CheckBox("cbExceptionCatchingThrowing", LambdaModel.of({ exceptionCatchingThrowing }, { v -> exceptionCatchingThrowing = v })))
        form.add(CheckBox("cbGeneralFixture", LambdaModel.of({ generalFixture }, { v -> generalFixture = v })))
        form.add(CheckBox("cbMysteryGuest", LambdaModel.of({ mysteryGuest }, { v -> mysteryGuest = v })))
        form.add(CheckBox("cbSleepyTest", LambdaModel.of({ sleepyTest }, { v -> sleepyTest = v })))
        form.add(CheckBox("cbSensitiveEquality", LambdaModel.of({ sensitiveEquality }, { v -> sensitiveEquality = v })))
        form.add(CheckBox("cbRedundantAssertion", LambdaModel.of({ redundantAssertion }, { v -> redundantAssertion = v })))
        form.add(CheckBox("cbPrintStatement", LambdaModel.of({ printStatement }, { v -> printStatement = v })))
        form.add(CheckBox("cbMagicNumberTest", LambdaModel.of({ magicNumberTest }, { v -> magicNumberTest = v })))
        form.add(CheckBox("cbResourceOptimism", LambdaModel.of({ resourceOptimism }, { v -> resourceOptimism = v })))
        form.add(CheckBox("cbIgnoredTest", LambdaModel.of({ ignoredTest }, { v -> ignoredTest = v })))
        form.add(CheckBox("cbUnknownTest", LambdaModel.of({ unknownTest }, { v -> unknownTest = v })))
        form.add(CheckBox("cbLazyTest", LambdaModel.of({ lazyTest }, { v -> lazyTest = v })))
        form.add(CheckBox("cbVerboseTest", LambdaModel.of({ verboseTest }, { v -> verboseTest = v })))
        form.add(TextField("verboseTestMaxStatements", LambdaModel.of({ verboseTestMaxStatements }, { v -> verboseTestMaxStatements = v })))

        form.add(object : Link<String>("lkSourceAssertionRoulette") {
            override fun onClick() { setResponsePage(SourcePage(AssertionRoulette())) }
        })
        form.add(object : Link<String>("lkSourceConditionalTestLogic") {
            override fun onClick() { setResponsePage(SourcePage(ConditionalTestLogic())) }
        })
        form.add(object : Link<String>("lkSourceConstructorInitialization") {
            override fun onClick() { setResponsePage(SourcePage(ConstructorInitialization())) }
        })
        form.add(object : Link<String>("lkSourceDefaultTest") {
            override fun onClick() { setResponsePage(SourcePage(DefaultTest())) }
        })
        form.add(object : Link<String>("lkSourceDependentTest") {
            override fun onClick() { setResponsePage(SourcePage(DependentTest())) }
        })
        form.add(object : Link<String>("lkSourceDuplicateAssert") {
            override fun onClick() { setResponsePage(SourcePage(DuplicateAssert())) }
        })
        form.add(object : Link<String>("lkSourceEagerTest") {
            override fun onClick() { setResponsePage(SourcePage(EagerTest())) }
        })
        form.add(object : Link<String>("lkSourceEmptyTest") {
            override fun onClick() { setResponsePage(SourcePage(EmptyTest())) }
        })
        form.add(object : Link<String>("lkSourceExceptionCatchingThrowing") {
            override fun onClick() { setResponsePage(SourcePage(ExceptionCatchingThrowing())) }
        })
        form.add(object : Link<String>("lkSourceGeneralFixture") {
            override fun onClick() { setResponsePage(SourcePage(GeneralFixture())) }
        })
        form.add(object : Link<String>("IgnoredTest") {
            override fun onClick() { setResponsePage(SourcePage(IgnoredTest())) }
        })
        form.add(object : Link<String>("LazyTest") {
            override fun onClick() { setResponsePage(SourcePage(LazyTest())) }
        })
        form.add(object : Link<String>("MagicNumberTest") {
            override fun onClick() { setResponsePage(SourcePage(MagicNumberTest())) }
        })
        form.add(object : Link<String>("MysteryGuest") {
            override fun onClick() { setResponsePage(SourcePage(MysteryGuest())) }
        })
        form.add(object : Link<String>("PrintStatement") {
            override fun onClick() { setResponsePage(SourcePage(PrintStatement())) }
        })
        form.add(object : Link<String>("RedundantAssertion") {
            override fun onClick() { setResponsePage(SourcePage(RedundantAssertion())) }
        })
        form.add(object : Link<String>("ResourceOptimism") {
            override fun onClick() { setResponsePage(SourcePage(ResourceOptimism())) }
        })
        form.add(object : Link<String>("SensitiveEquality") {
            override fun onClick() { setResponsePage(SourcePage(SensitiveEquality())) }
        })
        form.add(object : Link<String>("SleepyTest") {
            override fun onClick() { setResponsePage(SourcePage(SleepyTest())) }
        })
        form.add(object : Link<String>("UnknownTest") {
            override fun onClick() { setResponsePage(SourcePage(UnknownTest())) }
        })
        form.add(object : Link<String>("VerboseTest") {
            override fun onClick() { setResponsePage(SourcePage(VerboseTest())) }
        })

        add(form)
    }
}
