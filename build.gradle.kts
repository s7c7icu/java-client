buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.8.1")
    }
}

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("com.gradleup.shadow") version "9.2.2"
}

group = "icu.7c7.symservice"
version = project.ext["app_version"]!!

base {
    archivesName.set("javaclient")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.1")
//    implementation("org.bouncycastle:bcprov-jdk18on:1.82")
    implementation("org.json:json:20250517")
    implementation("software.pando.crypto:salty-coffee:1.1.1")
}

object Constants {
    const val INTERNAL_PACKAGE = "xland.s7c7icu.client.internal"
}


tasks.shadowJar {
    minimizeJar.set(true)
    relocate("org.json", "${Constants.INTERNAL_PACKAGE}.json")
    relocate("software.pando.crypto.nacl", "${Constants.INTERNAL_PACKAGE}.nacl")

    from(project.sourceSets.main.map { it.output }) {
        // Important for SPI registration
        include("module-info.class")
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
