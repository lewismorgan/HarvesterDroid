import de.dynamicfiles.projects.gradle.plugins.javafx.JavaFXGradlePluginExtension
import de.dynamicfiles.projects.gradle.plugins.javafx.tasks.JfxJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
}

buildscript {
  dependencies {
    classpath("de.dynamicfiles.projects.gradle.plugins:javafx-gradle-plugin:8.8.2")
  }
  repositories {
    jcenter()
  }
}

apply(plugin = "javafx-gradle-plugin")

val mainAppClass = "com.lewisjmorgan.harvesterdroid.app2.HarvesterDroidKt"
description = "Manage your resources across a number of Star Wars Galaxies resource trackers"

dependencies {
  compile(project(":api"))
  compile(project(":loader"))
  compile(project(":tracker-gh"))
  // OLD DEPS
  compile("de.saxsys:mvvmfx:1.6.0")
  compile("de.saxsys:mvvmfx-easydi:1.6.0")
  compile("org.controlsfx:controlsfx:8.40.13")
  compile("javax.persistence:persistence-api:1.0.2")
  compile("com.vladsch.flexmark:flexmark:0.22.22")
  compile("org.mongodb:mongodb-driver:3.5.0")
  compile("org.apache.logging.log4j:log4j-api:2.8.2")
  compile("org.apache.logging.log4j:log4j-core:2.8.2")
  compile("org.apache.logging.log4j:log4j-slf4j-impl:2.8.2")

  // NEW DEPS
  compile("no.tornado:tornadofx:1.7.18")
  // Use JUnit test framework
  testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  applicationName = "harvesterdroid"
  mainClassName = mainAppClass
}

distributions {
  main {
    baseName = "harvesterdroid"
  }
}

val commonManifestAttribs = mutableMapOf(
  "Implementation-Title" to "HarvesterDroid",
  "Implementation-Version" to project.version,
  "Implementation-Vendor" to "Waverunner"
)

val jfxManifestAttribs = mutableMapOf(
  "JavaFX-Preloader-Class" to "com.lewisjmorgan.harvesterdroid.loader.LauncherPreloader",
  "JavaFX-Application-Class" to "com.lewisjmorgan.harvesterdroid.app2.HarvesterDroidKt"
)

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
  withType<Jar> {
    appendix = ""
    archiveName = "harvesterdroid-${project.version}.jar"

    manifest {
      attributes(
        commonManifestAttribs.apply {
          putAll(mapOf(
            "Main-Class" to mainAppClass,
            "Class-Path" to project.configurations.runtime.get().files.joinToString(" ") { it.name }
          ))
          putAll(jfxManifestAttribs)
        }
      )
    }
  }
  withType<CreateStartScripts> {
    dependsOn("addRuntimeLibs")
  }
  register("distNative") {
    dependsOn("jfxNative")
    group = "distribution"
    description = "Bundles the project as a native distribution"
  }
}

configure<JavaFXGradlePluginExtension> {
  appName = "HarvesterDroid"
  nativeReleaseVersion = sanitizeVersionString(project.version.toString())
  bundler = "mac.app"
  mainClass = mainAppClass
  vendor = "Waverunner"
  isSkipJNLP = true

  jfxMainAppJarName = "harvesterdroid-${project.version}.jar"
}

fun sanitizeVersionString(str: String): String {
  val regex = Regex("[^-]+")
  val result = regex.find(str, 0)
  result?.value?.let {
    return it
  }
  return "0.1.0"
}