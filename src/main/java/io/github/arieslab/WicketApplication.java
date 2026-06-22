package io.github.arieslab;

import io.github.arieslab.base.CSVCore;
import io.github.arieslab.base.GitCore;
import io.github.arieslab.base.JNose;
import io.github.arieslab.business.ProjetoBusiness;
import io.github.arieslab.entities.Projeto;
import io.github.arieslab.pages.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import io.github.arieslab.pages.HomePage;
import de.agilecoders.wicket.core.Bootstrap;
import org.apache.wicket.response.filter.AjaxServerAndClientTimeFilter;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WicketApplication extends WebApplication {

    private static final Logger LOGGER = Logger.getLogger(WicketApplication.class.getName());

    @Autowired
    private ProjetoBusiness projetoBusiness;

    public static boolean COBERTURA_ON = false;

    public static String USERHOME;

    public static String JNOSE_PROJECTS_FOLDER;

    public static String JNOSE_PATH;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public void init() {
        super.init();
        getCspSettings().blocking().disabled();
        Bootstrap.install(this);

        getResourceSettings().setResourcePollFrequency(java.time.Duration.ofMinutes(1));
        getApplicationSettings().setUploadProgressUpdatesEnabled(true);
        getRequestCycleSettings().addResponseFilter(new AjaxServerAndClientTimeFilter());
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getDebugSettings().setAjaxDebugModeEnabled(false);
        // don't throw exceptions for missing translations
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        // enable ajax debug etc.
        getDebugSettings().setDevelopmentUtilitiesEnabled(false);
        // make markup friendly as in deployment-mode
        getMarkupSettings().setStripWicketTags(false);

        File fileRoot = new File(".");

        JNOSE_PATH = fileRoot.getAbsolutePath();
        LOGGER.log(Level.INFO, "JNOSE Path: {0}", JNOSE_PATH);

        USERHOME = System.getProperty("user.home");

        Runtime runTime = Runtime.getRuntime();

        String strTmp = System.getProperty("java.io.tmpdir");
        LOGGER.log(Level.INFO, "OS: {0} {1} | JDK: {2} {3} | Cores: {4} | Mem free: {5} | Mem total: {6}",
                new Object[]{System.getProperty("os.name"), System.getProperty("os.version"),
                        System.getProperty("java.version"), System.getProperty("java.vm.version"),
                        runTime.availableProcessors(), runTime.freeMemory(), runTime.totalMemory()});
        LOGGER.log(Level.FINE, "Temp dir: {0} | User home: {1}", new Object[]{strTmp, USERHOME});

        JNOSE_PROJECTS_FOLDER = USERHOME + File.separator + ".jnose_projects" + File.separator;
        LOGGER.log(Level.INFO, "JNose Projects folder: {0}", JNOSE_PROJECTS_FOLDER);

        CSVCore.load(this);

        File file = new File(JNOSE_PROJECTS_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        this.mountPage("/projects", ProjetosPage.class);
        this.mountPage("/byclasstest", ByClassTestPage.class);
        this.mountPage("/bytestsmells", ByTestSmellsPage.class);
        this.mountPage("/evolution", EvolutionPage.class);
        this.mountPage("/config", ConfigPage.class);
        this.mountPage("/research", ResearchPage.class);


        salvarProjeto(JNOSE_PROJECTS_FOLDER);
    }

    public List<Path> findJavaProjects(Path root) {
        try {
            try (Stream<Path> stream = Files.walk(root)) {
                return stream
                        .filter(Files::isDirectory)
                        .filter(p -> p.endsWith("src/main/java"))
                        .map(p -> root.relativize(p).getParent().getParent().getParent())
                        .map(root::resolve)
                        .distinct()
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to find Java projects in: " + root, e);
        }

        return null;
    }

    void salvarProjeto(String javaProjectsFolder){
        Path root = Paths.get(javaProjectsFolder);
        List<Path> javaProjects = findJavaProjects(root);



        for (Path javaProject : javaProjects) {
            Projeto projeto = new Projeto();
            projeto.setPath(javaProject.toString());

            projeto.setJunitVersion(JNose.getJUnitVersion(javaProject.toString()).toString());
            String lastFolder = javaProject.getFileName().toString();
            projeto.setName(lastFolder);
            projeto.setStars(0);
            Path projectFoldetGit = findGitRoot(javaProject);
            String urlGit = GitCore.getURL(projectFoldetGit.toString());
            projeto.setUrl(urlGit);
            //projeto.setDateUpdate(GitCore.getLastCommit(javaProject.toString()).get(0).date);
            projeto.setDateUpdate(new Date());

            Projeto projetoExiste = projetoBusiness.getProjetoByName(projeto.getName());

            if (projetoExiste == null) {
                projetoBusiness.save(projeto);
            }

        }
    }


    public Path findGitRoot(Path start) {
        Path current = start.toAbsolutePath();
        while (current != null) {
            if (Files.exists(current.resolve(".git"))) {
                return current;
            }
            current = current.getParent(); // sobe um nível
        }
        return null; // não encontrou
    }


}
