package io.github.arieslab.base.cobertura;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class BuildExecutor {

    private static final Logger LOGGER = Logger.getLogger(BuildExecutor.class.getName());

    private BuildExecutor() {}

    public static BuildType detect(File projectDir) {
        if (new File(projectDir, "pom.xml").exists()) return BuildType.MAVEN;
        if (new File(projectDir, "build.gradle").exists() || new File(projectDir, "build.gradle.kts").exists())
            return BuildType.GRADLE;
        if (new File(projectDir, "build.xml").exists()) return BuildType.ANT;
        if (new File(projectDir, "build.sbt").exists()) return BuildType.SBT;
        return BuildType.UNKNOWN;
    }

    public static boolean compile(File projectDir) {
        var type = detect(projectDir);
        var cmd = buildCommand(projectDir, type);
        if (cmd == null || cmd.isEmpty()) {
            LOGGER.log(Level.WARNING, "No compile command for build type: {0}", type);
            return false;
        }

        LOGGER.log(Level.INFO, "Compiling project with: {0}", cmd);

        var pb = new ProcessBuilder(cmd);
        pb.directory(projectDir);
        pb.redirectErrorStream(true);

        try {
            var process = pb.start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            var exitCode = process.waitFor();
            LOGGER.log(Level.INFO, "Compilation exit code: {0}", exitCode);
            return exitCode == 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Compilation failed", e);
            return false;
        }
    }

    private static List<String> buildCommand(File projectDir, BuildType type) {
        return switch (type) {
            case MAVEN -> {
                var mvn = findTool(projectDir, "mvnw", "mvn");
                yield List.of(mvn, "test-compile");
            }
            case GRADLE -> {
                var gradle = findTool(projectDir, "gradlew", "gradle");
                yield List.of(gradle, "testClasses");
            }
            case ANT -> {
                var ant = findTool("ant");
                yield ant != null ? List.of(ant) : null;
            }
            case SBT -> {
                var sbt = findTool("sbt");
                yield sbt != null ? List.of(sbt, "test:compile") : null;
            }
            case UNKNOWN -> {
                var javac = findTool("javac");
                if (javac != null) {
                    yield List.of(javac, "-d", "bin", findSources(projectDir));
                }
                yield null;
            }
        };
    }

    private static String findTool(File projectDir, String... names) {
        for (var name : names) {
            var local = new File(projectDir, name);
            if (local.exists()) return local.getAbsolutePath();
            var path = findOnPath(name);
            if (path != null) return path;
        }
        return null;
    }

    private static String findTool(String name) {
        return findOnPath(name);
    }

    private static String findOnPath(String name) {
        var pathEnv = System.getenv("PATH");
        if (pathEnv == null) return null;
        for (var dir : pathEnv.split(File.pathSeparator)) {
            var f = new File(dir, name);
            if (f.exists()) return f.getAbsolutePath();
            if (name.indexOf('.') == -1) {
                var fWindows = new File(dir, name + ".exe");
                if (fWindows.exists()) return fWindows.getAbsolutePath();
            }
        }
        return null;
    }

    private static String findSources(File projectDir) {
        var src = new File(projectDir, "src");
        if (!src.isDirectory()) return projectDir.getAbsolutePath();
        var sb = new StringBuilder();
        try (var files = java.nio.file.Files.walk(src.toPath())) {
            files.filter(p -> p.toString().endsWith(".java"))
                .limit(1)
                .forEach(p -> {
                    if (sb.length() > 0) sb.append(File.pathSeparator);
                    sb.append(p.getParent().toString());
                });
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Error scanning sources", e);
        }
        return sb.length() > 0 ? sb.toString() : projectDir.getAbsolutePath();
    }
}
