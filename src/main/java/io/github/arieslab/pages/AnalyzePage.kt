package io.github.arieslab.pages

import io.github.arieslab.core.Config
import io.github.arieslab.core.JNoseCore
import io.github.arieslab.dto.TestClass
import io.github.arieslab.dto.TestSmell
import io.github.arieslab.pages.base.BasePage
import io.github.arieslab.pages.modals.ModalView
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxButton
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.upload.FileUpload
import org.apache.wicket.markup.html.form.upload.FileUploadField
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.request.cycle.RequestCycle
import org.apache.wicket.request.http.WebRequest
import org.apache.wicket.util.lang.Bytes
import jakarta.servlet.http.HttpServletRequest
import java.io.File
import java.nio.file.Files
import java.util.logging.Logger

class AnalyzePage : BasePage {

    companion object {
        private val LOGGER = Logger.getLogger(AnalyzePage::class.java.name)
    }

    private val fileInList = mutableListOf<String>()
    private lateinit var container: WebMarkupContainer
    private lateinit var listview: ListView<TestSmell>
    private val descriptions = mutableMapOf<String, String>()

    constructor() : this(mutableListOf())

    constructor(listaTestSmellBeans: List<TestSmell>) : super("AnalyzePage") {
        fileInList.add("//zero")

        loadDescriptions()

        val req = RequestCycle.get().request as WebRequest
        val httpReq = req.containerRequest as HttpServletRequest
        val clientAddress = httpReq.remoteHost

        val containerFeedback = WebMarkupContainer("containerFeedback").apply {
            outputMarkupId = true
        }
        add(containerFeedback)

        val notificationPanel = NotificationPanel("feedback").apply { outputMarkupId = true }
        containerFeedback.add(notificationPanel)

        val form = Form<Unit>("fom").apply {
            isMultiPart = true
            maxSize = Bytes.kilobytes(1000)
            outputMarkupId = true
        }

        val fileUpload1 = FileUploadField("fileUpload1").apply { isRequired = true }
        form.add(fileUpload1)

        val fileUpload2 = FileUploadField("fileUpload2").apply { isRequired = false }
        form.add(fileUpload2)

        form.add(object : AjaxButton("btSubmit") {
            override fun onSubmit(target: AjaxRequestTarget) {
                val uploadedFile1 = fileUpload1.fileUpload
                val uploadedFile2 = fileUpload2.fileUpload

                var classTestFile: File? = null
                var classProductionFile: File? = null

                if (uploadedFile1 != null) {
                    try {
                        classTestFile = File(uploadedFile1.clientFileName)
                        uploadedFile1.writeTo(classTestFile)
                    } catch (e: Exception) {
                        LOGGER.warning("Failed to process uploaded file 1: $e")
                    }
                }

                try {
                    Files.lines(classTestFile!!.absoluteFile.toPath()).forEach { line -> fileInList.add(line) }
                } catch (e: Exception) {
                    LOGGER.warning("Failed to read uploaded file: $e")
                }

                if (uploadedFile2 != null) {
                    try {
                        classProductionFile = File(uploadedFile2.clientFileName)
                        uploadedFile2.writeTo(classProductionFile)
                    } catch (e: Exception) {
                        LOGGER.warning("Failed to process uploaded file 2: $e")
                    }
                }

                val testClass = TestClass()
                testClass.name = uploadedFile1!!.clientFileName
                testClass.pathFile = classTestFile!!.absolutePath
                testClass.productionFile = classProductionFile?.absolutePath ?: ""
                testClass.projectName = ""
                testClass.junitVersion = TestClass.JunitVersion.JUnit4

                val jNoseCore = JNoseCore(loadConfig(!testClass.productionFile.isNullOrBlank()))
                jNoseCore.getTestSmells(testClass)

                val listaTestSmellBean = testClass.listTestSmell
                listview.list = listaTestSmellBean

                target.add(container, form, containerFeedback)

                info("ClassTest: ${uploadedFile1.clientFileName} - Processed - ${listaTestSmellBean.size} TestSmells found.")

                if (classProductionFile != null) {
                    info("ClassProduction: ${uploadedFile2?.clientFileName}")
                    classProductionFile.delete()
                }

                classTestFile.delete()
            }
        })

        add(form)

        listview = object : ListView<TestSmell>("listview", listaTestSmellBeans) {
            override fun populateItem(item: ListItem<TestSmell>) {
                val testSmell = item.modelObject

                item.add(Label("nome", testSmell.name))
                item.add(Label("method", testSmell.method))
                item.add(Label("range", testSmell.range))

                val contentDescription = descriptions[testSmell.name]
                val content = "$contentDescription<br><br><b>You code:</b><br><pre><br>${getLines(testSmell.range)}<br></pre> <br>"

                val modal = ModalView("modal", testSmell.name, content)
                item.add(modal)

                item.add(object : AjaxLink<Any>("lkModal") {
                    override fun onClick(ajaxRequestTarget: AjaxRequestTarget) {
                        modal.show(true)
                        ajaxRequestTarget.add(modal)
                    }
                })
            }
        }

        container = WebMarkupContainer("container").apply {
            outputMarkupId = true
            add(listview)
        }
        add(container)
    }

    private fun loadConfig(withClassProduction: Boolean): Config {
        return object : Config {
            override fun assertionRoulette() = true
            override fun conditionalTestLogic() = true
            override fun constructorInitialization() = true
            override fun defaultTest() = true
            override fun dependentTest() = true
            override fun duplicateAssert() = true
            override fun eagerTest() = withClassProduction
            override fun emptyTest() = true
            override fun exceptionCatchingThrowing() = true
            override fun generalFixture() = true
            override fun mysteryGuest() = true
            override fun printStatement() = true
            override fun redundantAssertion() = true
            override fun sensitiveEquality() = true
            override fun verboseTest() = true
            override fun sleepyTest() = true
            override fun lazyTest() = withClassProduction
            override fun unknownTest() = true
            override fun ignoredTest() = true
            override fun resourceOptimism() = true
            override fun magicNumberTest() = true
            override fun maxStatements() = 30
        }
    }

    private fun loadDescriptions() {
        descriptions["Unknown Test"] = "A test method without a assertion condition, the test will always be valid, not resulting in an exception. This programming practice makes it difficult to understand the test."
        descriptions["Sleepy Test"] = "Developers introduce this test smell when they need to pause the execution of instructions in a test method for a certain period and continue the execution."
        descriptions["Assertion Roulette"] = "This test smell occurs when the test method has a series of assertions without a description. If an assertion fails, it is not known which one generated the failure and its reason."
        descriptions["Conditional Test Logic"] = "Tests containing conditional logic (IF instructions or loops)."
        descriptions["Redundant Assertion"] = "This smell occurs when the test methods contain assertion statements that are always true or false. A test is intended to return a binary result, regardless of whether the desired result is correct or not, and must not return the same output, regardless of the input."
        descriptions["Sensitive Equality"] = "It is quick and easy to write equality checks using 'string'. A typical way is to calculate a real result, map it to a string, which is then compared to a literal string that represents the expected value. Such tests, however, can depend on many irrelevant details, such as commas, quotes, spaces, etc. Whenever a 'string' is changed, the tests start to fail. The solution is to replace the equality checks using 'string' with real equality checks."
        descriptions["Duplicate Assert"] = "This smell occurs when a test method tests the same condition several times on the same test method."
        descriptions["Constructor Initialization"] = "Test methods that feature a constructor. Ideally, the test suite should not have a constructor. The initialization of the fields must be in the setUp() method. Developers who are unaware of the purpose of the setUp() method would allow this test smell by creating a constructor for the test suite."
        descriptions["IgnoredTest"] = "Starting with JUnit 4, developers are provided with the ability to prevent the execution of test methods. However, these ignored test methods result in overhead in terms of compilation time and an increase in code complexity and understanding time."
        descriptions["Resource Optimism"] = "Test code that makes optimistic assumptions about the existence or absence of a particular external resource, and the state of that external resource (such as private directories or database tables) can cause non-deterministic behavior in the test results. The situation in which the tests run well at one time and fail at another is not a situation that the test should take place."
        descriptions["Magic Number Test"] = "Many 'Magic Numbers' or Strings used when creating objects that are likely to result in an unrepeatable test."
        descriptions["EmptyTest"] = "Test methods that do not contain executable instructions."
        descriptions["Exception Catching Throwing"] = "This test smell occurs when the approval or disapproval of a test method explicitly depends on the production method that generates an exception."
        descriptions["General Fixture"] = "In the JUnit framework, a programmer can write a 'setUp()' method that will be executed before each test method to create an environment for the tests to be run. The test smell becomes evident when the 'setUp()' environment is very general and the tests only need part of this configuration. The 'setUp()' methods start to get big and their understanding is reduced, and with the addition of functions, it starts to get slow. The danger of having a test that takes too long to complete, interfering with the development process, encourages programmers not to run the tests."
        descriptions["Mystery Guest"] = "When a test uses external resources, such as a file necessary for its execution, this external resource does not make the test autonomous. Consequently, there is not enough information to understand the tested functionality, making it difficult to use this test as documentation. In addition, these external resources can be shared. And its use introduces hidden dependencies: if any force changes or excludes this feature, the tests begin to fail."
        descriptions["Print Statement"] = "The printing instructions in the unit tests are redundant, as the unit tests are performed as part of an automated script. It consumes resources or increases the execution time."
        descriptions["Lazy Test"] = "This test smell occurs when several test methods check the same method using the same equipment (but, for example, they check the values of different instance variables). These tests are usually meaningful only when considered together."
        descriptions["Eager Test"] = "When a test method checks several methods of the object to be tested, it is difficult to read and understand and, therefore, it is more difficult to use as documentation. In addition, it makes tests more dependent on each other and more difficult to maintain."
        descriptions["Verbose Test"] = "Excess test code or Conditional Test Logic. Difficult to verify its accuracy and more likely to contain errors."
    }

    private fun getLines(range: String): String {
        val lines = StringBuilder()

        when {
            range.contains(",") -> {
                val r = range.split(",")
                for (number in r) {
                    if (!number.isBlank()) {
                        if (number.contains("-")) {
                            getLinesByRange(lines, number)
                        } else {
                            val line = number.trim().toInt()
                            lines.append(getLine(line - 2))
                            lines.append(getLine(line - 1))
                            lines.append("<b>${fileInList[line]}</b><br>")
                            lines.append(getLine(line + 1))
                            lines.append(getLine(line + 2))
                        }
                    }
                }
            }
            range.contains("-") -> getLinesByRange(lines, range)
            else -> {
                val line = range.toInt()
                lines.append(getLine(line - 2))
                lines.append(getLine(line - 1))
                lines.append("<b>${fileInList[line]}</b><br>")
                lines.append(getLine(line + 1))
                lines.append(getLine(line + 2))
            }
        }

        return lines.toString()
    }

    private fun getLinesByRange(lines: StringBuilder, range: String) {
        val r = range.trim().split("-")
        val start = r[0].toInt()
        val end = r[1].toInt()
        if (start == end) {
            lines.append(getLine(start - 2))
            lines.append(getLine(start - 1))
            lines.append("<b>${fileInList[start]}</b><br>")
            lines.append(getLine(start + 1))
            lines.append(getLine(start + 2))
        } else {
            lines.append(getLine(start - 2))
            lines.append(getLine(start - 1))
            fileInList.subList(start, end + 1).forEach { line -> lines.append("<b>$line</b><br>") }
            lines.append(getLine(end + 2))
            lines.append(getLine(end + 3))
        }
    }

    private fun getLine(line: Int): String {
        return if (line > 0 && line < fileInList.size) "${fileInList[line]}<br>" else ""
    }
}
