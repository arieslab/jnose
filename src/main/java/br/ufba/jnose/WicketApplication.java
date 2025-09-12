package br.ufba.jnose;

import br.ufba.jnose.base.CSVCore;
import br.ufba.jnose.base.GitCore;
import br.ufba.jnose.base.JNose;
import br.ufba.jnose.business.ProjetoBusiness;
import br.ufba.jnose.entities.Projeto;
import br.ufba.jnose.pages.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import br.ufba.jnose.pages.HomePage;
import de.agilecoders.wicket.core.Bootstrap;
import org.apache.wicket.response.filter.AjaxServerAndClientTimeFilter;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WicketApplication extends WebApplication {

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
        Bootstrap.install(this);

        getResourceSettings().setResourcePollFrequency(Duration.ONE_MINUTE);
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
        System.out.println("JNOSE Path: " + JNOSE_PATH);

        USERHOME = System.getProperty("user.home");
        System.out.println("OS current user home directory is " + USERHOME);

        Runtime runTime = Runtime.getRuntime();

        String strTmp = System.getProperty("java.io.tmpdir");
        System.out.println("OS current temporary directory: " + strTmp);
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("OS user home directory is " + USERHOME);
        System.out.println("JDK Version: " + System.getProperty("java.version"));
        System.out.println("JDK VM Version: " + System.getProperty("java.vm.version"));
        System.out.println("Cores: " + runTime.availableProcessors());
        System.out.println("Mem free: " + runTime.freeMemory() + " bytes");
        System.out.println("Mem total: " + runTime.totalMemory() + " bytes");


        JNOSE_PROJECTS_FOLDER = USERHOME + File.separator + ".jnose_projects" + File.separator;
        System.out.println("JNose Projects folder: " + JNOSE_PROJECTS_FOLDER);

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
            e.printStackTrace();
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

            System.out.println(projeto);

            projetoBusiness.save(projeto);
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
