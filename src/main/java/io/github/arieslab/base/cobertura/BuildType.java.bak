package io.github.arieslab.base.cobertura;

import java.io.File;

public enum BuildType {
    MAVEN("target/classes", "target/test-classes", "target/jacoco.exec", "src/main/java"),
    GRADLE("build/classes/java/main", "build/classes/java/test", "build/jacoco.exec", "src/main/java"),
    ANT("bin", "bin", "build/jacoco.exec", "src"),
    SBT("target/scala-*/classes", "target/scala-*/test-classes", "target/jacoco.exec", "src/main/java"),
    UNKNOWN("target/classes", "target/test-classes", "target/jacoco.exec", "src/main/java");

    private final String classesDir;
    private final String testClassesDir;
    private final String jacocoExecPath;
    private final String sourceDir;

    BuildType(String classesDir, String testClassesDir, String jacocoExecPath, String sourceDir) {
        this.classesDir = classesDir;
        this.testClassesDir = testClassesDir;
        this.jacocoExecPath = jacocoExecPath;
        this.sourceDir = sourceDir;
    }

    public File getClassesDir(File projectDir) {
        return new File(projectDir, classesDir);
    }

    public File getTestClassesDir(File projectDir) {
        return new File(projectDir, testClassesDir);
    }

    public File getJacocoExecFile(File projectDir) {
        return new File(projectDir, jacocoExecPath);
    }

    public File getSourceDir(File projectDir) {
        return new File(projectDir, sourceDir);
    }
}
