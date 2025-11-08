import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import proguard.gradle.ProGuardTask
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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
    implementation("org.bouncycastle:bcprov-jdk18on:1.82")
    implementation("org.json:json:20250517")
}

val multiReleaseMinimizedJar: Provider<RegularFile> = base.libsDirectory.file(provider {
    "${base.archivesName.get()}-${project.version}-mro.jar"
})
val slimJar: Provider<RegularFile> = base.libsDirectory.file(provider {
    "${base.archivesName.get()}-${project.version}-slim.jar"
})

tasks.register("minimizeMultiRelease") {
    notCompatibleWithConfigurationCache("It sucks")

    val mappedPrefixes: Collection<String> = (9..17).map {
        "META-INF/versions/$it/"
    }

    fun ZipEntry.releaseOf() : Int = name.split('/', limit=4)[2].toInt()
    fun ZipEntry.realPath() : String = name.split('/', limit=4)[3]

    dependsOn("shadowJar")
    doLast {
        val inputFile: Provider<RegularFile> = tasks.shadowJar.flatMap { it.archiveFile }
        val outputFile: Provider<RegularFile> = multiReleaseMinimizedJar

        ZipFile(file(inputFile)).use { zipFile ->
            val overrideMap: MutableMap<String, ZipEntry?> = mutableMapOf()

            zipFile.stream().forEach { entry ->
                if (mappedPrefixes.any(entry.name::startsWith)) {
                    val originalPath = entry.realPath()
                    val originalEntry: ZipEntry? = overrideMap[originalPath]
                    if (originalEntry == null) {    // absent; or marked null (shouldn't happen)
                        overrideMap[originalPath] = entry
                    } else {
                        if (entry.releaseOf() > originalEntry.releaseOf()) {
                            // override
                            overrideMap[originalPath] = entry
                        }
                    }
                    // remove me
                    overrideMap[entry.name] = null
                }
            }

            ZipOutputStream(file(outputFile).outputStream().buffered()).use { zos ->
                zipFile.stream().forEach entryLoop@ { entry ->
                    val inStream: InputStream
                    if (entry.name !in overrideMap) {
                        // not overridden
                        inStream = zipFile.getInputStream(entry)!!
                    } else {
                        val overrideEntry: ZipEntry = overrideMap[entry.name] ?: return@entryLoop // removed
                        inStream = zipFile.getInputStream(overrideEntry)
                    }

                    zos.putNextEntry(entry)
                    try { inStream.copyTo(zos) } finally { zos.closeEntry() }
                }
            }
        }
    }
}

object Constants {
    const val INTERNAL_PACKAGE = "xland.s7c7icu.client.internal"
}


tasks.shadowJar {
    minimizeJar.set(false)
    addMultiReleaseAttribute.set(true)
    relocate("org.bouncycastle", "${Constants.INTERNAL_PACKAGE}.bouncycastle")
    relocate("org.json", "${Constants.INTERNAL_PACKAGE}.json")

    finalizedBy("minimizeMultiRelease")
}

val minimizeSource: Configuration by configurations.creating

dependencies {
    add("minimizeSource", files(multiReleaseMinimizedJar))
}

tasks.register<ShadowJar>("minimizeJar") {
    dependsOn("minimizeMultiRelease")
    configurations.set(mutableSetOf(minimizeSource))
    minimizeJar.set(true)
    archiveClassifier.set("minimized")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.build {
    dependsOn("minimizeJar")
}
