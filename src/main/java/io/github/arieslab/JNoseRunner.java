package io.github.arieslab;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class JNoseRunner {

    private static final Logger LOGGER = Logger.getLogger(JNoseRunner.class.getName());

    private static final String[] ADD_OPENS = {
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.base/java.text=ALL-UNNAMED",
            "--add-opens", "java.desktop/java.awt.font=ALL-UNNAMED"
    };

    public static void main(String[] args) throws Exception {
        if (needsAddOpens()) {
            restartWithAddOpens(args);
            return;
        }
        start(args);
    }

    private static boolean needsAddOpens() {
        return System.getProperty("jnose.addopens") == null;
    }

    private static void restartWithAddOpens(String[] args) throws Exception {
        String jarPath = JNoseRunner.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessHandle.current().info().command().orElse("java"));
        for (int i = 0; i < ADD_OPENS.length; i++) {
            cmd.add(ADD_OPENS[i]);
        }
        cmd.add("-Djnose.addopens=true");
        cmd.add("-jar");
        cmd.add(jarPath);
        for (String arg : args) {
            cmd.add(arg);
        }

        LOGGER.info("Restarting with --add-opens flags...");
        new ProcessBuilder(cmd)
                .inheritIO()
                .start();
    }

    private static void start(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        String jarPath = JNoseRunner.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        Path tempDir = Files.createTempDirectory("jnose-");
        tempDir.toFile().deleteOnExit();

        extractWebapp(jarPath, tempDir);

        Server server = new Server(port);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(tempDir.toAbsolutePath().toString());
        webapp.setParentLoaderPriority(true);
        webapp.setThrowUnavailableOnStartupException(true);
        webapp.setTempDirectory(Files.createTempDirectory("jnose-work-").toFile());

        server.setHandler(webapp);
        server.start();
        LOGGER.info("JNose started on http://localhost:" + port);
        server.join();
    }

    private static void extractWebapp(String jarPath, Path targetDir) throws IOException {
        File file = new File(jarPath);
        if (file.isDirectory()) {
            Path webappSrc = Paths.get("src/main/webapp");
            if (Files.exists(webappSrc)) {
                try (var paths = Files.walk(webappSrc)) {
                    paths.filter(Files::isRegularFile).forEach(src -> {
                        try {
                            Path dest = targetDir.resolve(webappSrc.relativize(src).toString());
                            Files.createDirectories(dest.getParent());
                            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                }
            }
        } else {
            try (JarFile jar = new JarFile(file)) {
                jar.stream()
                    .filter(e -> e.getName().startsWith("webapp/") && !e.isDirectory())
                    .forEach(e -> {
                        try {
                            String relativePath = e.getName().substring("webapp/".length());
                            Path out = targetDir.resolve(relativePath);
                            Files.createDirectories(out.getParent());
                            Files.copy(jar.getInputStream(e), out, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    });
            }
        }
    }
}
