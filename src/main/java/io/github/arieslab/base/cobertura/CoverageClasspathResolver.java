package io.github.arieslab.base.cobertura;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class CoverageClasspathResolver {

    private static final Logger LOGGER = Logger.getLogger(CoverageClasspathResolver.class.getName());

    private static final String MAVEN_REPO = System.getProperty("user.home") + "/.m2/repository";

    private CoverageClasspathResolver() {}

    public static String resolve(File projectDir) {
        var entries = new LinkedHashSet<String>();

        if (new File(projectDir, "pom.xml").exists()) {
            addIfExists(entries, projectDir, "target/classes");
            addIfExists(entries, projectDir, "target/test-classes");
            resolveMavenDeps(projectDir, entries);
        } else if (new File(projectDir, "build.gradle").exists()
                || new File(projectDir, "build.gradle.kts").exists()) {
            addIfExists(entries, projectDir, "build/classes/java/main");
            addIfExists(entries, projectDir, "build/classes/java/test");
            addIfExists(entries, projectDir, "build/libs");
            scanDir(entries, projectDir, "libs");
        } else {
            addIfExists(entries, projectDir, "bin");
            addIfExists(entries, projectDir, "out");
        }

        scanDir(entries, projectDir, "lib");
        scanDir(entries, projectDir, "libs");
        scanDir(entries, projectDir, "WEB-INF/lib");
        scanDir(entries, projectDir, "WEB-INF/classes");

        return String.join(File.pathSeparator, entries);
    }

    private static void resolveMavenDeps(File projectDir, Set<String> entries) {
        var visited = new HashSet<String>();
        try {
            resolvePomRecursive(new File(projectDir, "pom.xml"), entries, visited);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to resolve Maven dependencies from " + projectDir, e);
        }
    }

    private static void resolvePomRecursive(File pomFile, Set<String> entries, Set<String> visited) {
        if (pomFile == null || !pomFile.exists()) return;

        try (var reader = new FileReader(pomFile)) {
            var model = new MavenXpp3Reader().read(reader);
            var props = collectProperties(model);

            // Parent POM
            if (model.getParent() != null) {
                var parentPom = findMavenArtifact(
                    model.getParent().getGroupId(),
                    model.getParent().getArtifactId(),
                    resolveVersion(model.getParent().getVersion(), props));
                if (parentPom != null) {
                    var parentDir = parentPom.getParentFile();
                    addIfExists(entries, parentDir.getParentFile(), parentDir.getName() + ".jar");
                }
            }

            for (var dep : model.getDependencies()) {
                var key = dep.getGroupId() + ":" + dep.getArtifactId();
                if (visited.contains(key)) continue;
                visited.add(key);

                var version = resolveVersion(dep.getVersion(), props);
                if (version == null || version.isEmpty() || version.contains("${")) continue;

                var jar = findMavenArtifact(dep.getGroupId(), dep.getArtifactId(), version);
                if (jar != null) {
                    entries.add(jar.getAbsolutePath());
                }

                // Recursively resolve transitive deps
                var depPom = findMavenArtifact(dep.getGroupId(), dep.getArtifactId(), version, "pom");
                if (depPom != null) {
                    resolvePomRecursive(depPom, entries, visited);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Skipping POM: " + pomFile, e);
        }
    }

    private static Properties collectProperties(Model model) {
        var props = new Properties();
        if (model.getParent() != null) {
            var parentPom = findMavenArtifact(
                model.getParent().getGroupId(),
                model.getParent().getArtifactId(),
                model.getParent().getVersion());
            if (parentPom != null) {
                try (var r = new FileReader(parentPom)) {
                    var parentModel = new MavenXpp3Reader().read(r);
                    props.putAll(parentModel.getProperties());
                } catch (Exception ignored) {}
            }
        }
        props.putAll(model.getProperties());
        props.put("project.version", model.getVersion() != null ? model.getVersion() : "");
        props.put("project.groupId", model.getGroupId());
        return props;
    }

    private static String resolveVersion(String version, Properties props) {
        if (version == null) return null;
        var resolved = version;
        while (resolved.contains("${")) {
            var start = resolved.indexOf("${");
            var end = resolved.indexOf("}", start);
            if (end == -1) break;
            var key = resolved.substring(start + 2, end);
            var val = props.getProperty(key);
            if (val == null) break;
            resolved = resolved.substring(0, start) + val + resolved.substring(end + 1);
        }
        return resolved;
    }

    private static File findMavenArtifact(String groupId, String artifactId, String version) {
        return findMavenArtifact(groupId, artifactId, version, "jar");
    }

    private static File findMavenArtifact(String groupId, String artifactId, String version, String type) {
        var path = MAVEN_REPO + "/" + groupId.replace('.', '/')
            + "/" + artifactId
            + "/" + version
            + "/" + artifactId + "-" + version + "." + type;
        var file = new File(path);
        return file.exists() ? file : null;
    }

    private static void addIfExists(Set<String> entries, File base, String relative) {
        var f = new File(base, relative);
        if (f.exists()) entries.add(f.getAbsolutePath());
    }

    private static void scanDir(Set<String> entries, File base, String relative) {
        var dir = new File(base, relative);
        if (!dir.isDirectory()) return;
        try (var files = Files.walk(dir.toPath()).filter(Files::isRegularFile)) {
            files.filter(p -> p.toString().endsWith(".jar"))
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .forEach(entries::add);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Skipping scan: " + dir, e);
        }
    }
}
