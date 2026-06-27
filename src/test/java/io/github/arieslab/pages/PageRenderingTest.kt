package io.github.arieslab.pages

import io.github.arieslab.WicketTestBase
import org.junit.jupiter.api.Test

class PageRenderingTest : WicketTestBase() {

    @Test
    fun testHomePageRenders() {
        tester.startPage(HomePage::class.java)
        tester.assertRenderedPage(HomePage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testConfigPageRenders() {
        tester.startPage(ConfigPage::class.java)
        tester.assertRenderedPage(ConfigPage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testProjetosPageRenders() {
        tester.startPage(ProjetosPage::class.java)
        tester.assertRenderedPage(ProjetosPage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testByClassTestPageRenders() {
        tester.startPage(ByClassTestPage::class.java)
        tester.assertRenderedPage(ByClassTestPage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testByTestSmellsPageRenders() {
        tester.startPage(ByTestSmellsPage::class.java)
        tester.assertRenderedPage(ByTestSmellsPage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testResearchPageRenders() {
        tester.startPage(ResearchPage::class.java)
        tester.assertRenderedPage(ResearchPage::class.java)
        tester.assertNoErrorMessage()
    }

    @Test
    fun testAnalyzePageRenders() {
        tester.startPage(AnalyzePage::class.java)
        tester.assertRenderedPage(AnalyzePage::class.java)
        tester.assertNoErrorMessage()
    }
}
