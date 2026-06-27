package io.github.arieslab

import de.agilecoders.wicket.core.Bootstrap
import io.github.arieslab.base.CSVCore
import io.github.arieslab.base.GitCore
import io.github.arieslab.base.JNose
import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.entities.Projeto
import io.github.arieslab.pages.*
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Date
import java.util.logging.Logger

@Component
open class WicketApplication : WebApplication() {

    companion object {
        private val LOGGER = Logger.getLogger(WicketApplication::class.java.name)
        var COBERTURA_ON = false
        var USERHOME = ""
        var JNOSE_PROJECTS_FOLDER = ""
        var JNOSE_PATH = ""
    }

    @Autowired
    private lateinit var projetoBusiness: ProjetoBusiness

    override fun getHomePage(): Class<out WebPage> = HomePage::class.java

    override fun init() {
        super.init()
        cspSettings.blocking().disabled()
        Bootstrap.install(this)

        resourceSettings.resourcePollFrequency = java.time.Duration.ofMinutes(1)
        applicationSettings.isUploadProgressUpdatesEnabled = true
        componentInstantiationListeners.add(SpringComponentInjector(this))
        requestCycleSettings.responseRequestEncoding = "UTF-8"
        markupSettings.defaultMarkupEncoding = "UTF-8"
        debugSettings.isAjaxDebugModeEnabled = false
        resourceSettings.throwExceptionOnMissingResource = false
        debugSettings.isDevelopmentUtilitiesEnabled = false
        markupSettings.stripWicketTags = false

        val fileRoot = File(".")
        JNOSE_PATH = fileRoot.absolutePath
        LOGGER.info("JNOSE Path: $JNOSE_PATH")

        USERHOME = System.getProperty("user.home")

        val runTime = Runtime.getRuntime()
        val strTmp = System.getProperty("java.io.tmpdir")
        LOGGER.info("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")} | JDK: ${System.getProperty("java.version")} ${System.getProperty("java.vm.version")} | Cores: ${runTime.availableProcessors()} | Mem free: ${runTime.freeMemory()} | Mem total: ${runTime.totalMemory()}")
        LOGGER.fine("Temp dir: $strTmp | User home: $USERHOME")

        JNOSE_PROJECTS_FOLDER = "$USERHOME${File.separator}.jnose_projects${File.separator}"
        LOGGER.info("JNose Projects folder: $JNOSE_PROJECTS_FOLDER")

        CSVCore.load(this)

        val file = File(JNOSE_PROJECTS_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }

        mountPage("/projects", ProjetosPage::class.java)
        mountPage("/byclasstest", ByClassTestPage::class.java)
        mountPage("/bytestsmells", ByTestSmellsPage::class.java)
        mountPage("/evolution", EvolutionPage::class.java)
        mountPage("/config", ConfigPage::class.java)
        mountPage("/research", ResearchPage::class.java)

        salvarProjeto(JNOSE_PROJECTS_FOLDER)
    }

    fun findJavaProjects(root: Path): List<Path>? {
        return try {
            Files.walk(root)
                .filter { it.toFile().isDirectory }
                .filter { it.endsWith("src/main/java") }
                .map { root.relativize(it).parent.parent.parent }
                .map { root.resolve(it) }
                .distinct()
                .toList()
        } catch (e: Exception) {
            LOGGER.warning("Failed to find Java projects in: $root")
            null
        }
    }

    fun salvarProjeto(javaProjectsFolder: String) {
        val root = Paths.get(javaProjectsFolder)
        val javaProjects = findJavaProjects(root) ?: return

        for (javaProject in javaProjects) {
            val projeto = Projeto()
            projeto.path = javaProject.toString()
            projeto.junitVersion = JNose.getJUnitVersion(javaProject.toString()).toString()
            val lastFolder = javaProject.fileName.toString()
            projeto.name = lastFolder
            projeto.stars = 0
            val projectFolderGit = findGitRoot(javaProject)
            val urlGit = GitCore.getURL(projectFolderGit.toString())
            projeto.url = urlGit
            projeto.dateUpdate = Date()

            val projetoExiste = projetoBusiness.getProjetoByName(projeto.name)
            if (projetoExiste == null) {
                projetoBusiness.save(projeto)
            }
        }
    }

    fun findGitRoot(start: Path): Path? {
        var current = start.toAbsolutePath()
        while (current != null) {
            if (Files.exists(current.resolve(".git"))) return current
            current = current.parent
        }
        return null
    }
}
