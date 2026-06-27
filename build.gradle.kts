import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.4.0"
    id("org.springframework.boot") version "4.0.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.arieslab"
version = "2.5.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repository.apache.org/content/repositories/snapshots/")
    }
}

val wicketVersion = "10.9.1"
val hibernateVersion = "7.4.1.Final"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.7")
    }
}

dependencies {
    // Project
    implementation("io.github.arieslab:jnose-core:0.9.4")

    // Kotlin
    implementation(kotlin("stdlib"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Hibernate ORM
    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion")
    implementation("org.hibernate.orm:hibernate-community-dialects:$hibernateVersion")
    implementation("org.springframework:spring-orm")

    // Database
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")

    // Wicket
    implementation("org.apache.wicket:wicket-core:$wicketVersion")
    implementation("org.apache.wicket:wicket-extensions:$wicketVersion")
    implementation("org.apache.wicket:wicket-spring:$wicketVersion")

    // Wicket Bootstrap
    implementation("de.agilecoders.wicket:wicket-bootstrap-core:7.0.14")
    implementation("de.agilecoders.wicket:wicket-bootstrap-extensions:7.0.14")
    implementation("de.agilecoders.wicket:wicket-bootstrap-themes:7.0.14")

    // Wicket Stuff
    implementation("org.wicketstuff:wicketstuff-jquery-ui-core:10.9.2")

    // Wicket proxy support
    implementation("org.objenesis:objenesis:3.4")

    // JSON
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")

    // Apache Commons
    implementation("commons-io:commons-io:2.14.0")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.google.guava:guava:32.0.0-jre")

    // Git
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.4.202507202350-r")

    // GitHub API
    implementation("org.kohsuke:github-api:1.116")

    // Java parser
    implementation("com.github.javaparser:javaparser-core:3.3.5")

    // JaCoCo
    implementation("org.jacoco:org.jacoco.core:0.8.12")
    implementation("org.jacoco:org.jacoco.report:0.8.12")
    implementation("org.jacoco:org.jacoco.agent:0.8.12")

    // Email
    implementation("com.sun.mail:jakarta.mail:2.0.2")

    // SLF4J
    implementation("org.slf4j:slf4j-api:2.0.16")

    // Maven model (used by jnose-core)
    implementation("org.apache.maven:maven-model:3.9.9")

    // Test - JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")

    // Test - JUnit 4 (compat)
    testImplementation("junit:junit:4.13.1")

    // Test - Mockito
    testImplementation("org.mockito:mockito-core")

    // Test - Wicket
    testImplementation("org.apache.wicket:wicket-tester:$wicketVersion")

    // Test - Spring Boot
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Test - JUnit Platform Launcher (required by Gradle 9.x)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

springBoot {
    mainClass.set("io.github.arieslab.JNoseApplication")
}

sourceSets {
    main {
        resources {
            srcDir("src/main/java")
            exclude("**/*.java")
        }
    }
    test {
        resources {
            srcDir("src/test/java")
            exclude("**/*.java")
        }
    }
}
