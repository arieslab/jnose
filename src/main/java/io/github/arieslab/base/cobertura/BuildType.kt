package io.github.arieslab.base.cobertura

import java.io.File

enum class BuildType(
    val classesDir: String,
    val testClassesDir: String,
    val jacocoExecPath: String,
    val sourceDir: String
) {
    MAVEN("target/classes", "target/test-classes", "target/jacoco.exec", "src/main/java"),
    GRADLE("build/classes/java/main", "build/classes/java/test", "build/jacoco.exec", "src/main/java"),
    ANT("bin", "bin", "build/jacoco.exec", "src"),
    SBT("target/scala-*/classes", "target/scala-*/test-classes", "target/jacoco.exec", "src/main/java"),
    UNKNOWN("target/classes", "target/test-classes", "target/jacoco.exec", "src/main/java");

    fun getClassesDir(projectDir: File) = File(projectDir, classesDir)
    fun getTestClassesDir(projectDir: File) = File(projectDir, testClassesDir)
    fun getJacocoExecFile(projectDir: File) = File(projectDir, jacocoExecPath)
    fun getSourceDir(projectDir: File) = File(projectDir, sourceDir)
}
