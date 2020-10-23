package br.ufba.jnose;

import br.ufba.jnose.core.CSVCore;
import br.ufba.jnose.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.response.filter.AjaxServerAndClientTimeFilter;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WicketApplication extends WebApplication {

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

        String strTmp = System.getProperty("java.io.tmpdir");
        System.out.println("OS current temporary directory: " + strTmp);
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("OS user home directory is " + USERHOME);
        System.out.println("JDK Version: " + System.getProperty("java.version"));
        System.out.println("JDK VM Version: " + System.getProperty("java.vm.version"));


        JNOSE_PROJECTS_FOLDER = USERHOME+ File.separator + ".jnose_projects" + File.separator;
        System.out.println("JNose Projects folder: " + JNOSE_PROJECTS_FOLDER);

        super.init();
        this.getMarkupSettings().setStripWicketTags(true);
        this.getDebugSettings().setAjaxDebugModeEnabled(false);
        CSVCore.load(this);

        File file = new File(JNOSE_PROJECTS_FOLDER);
        if(!file.exists()){
            file.mkdirs();
        }
    }
}
