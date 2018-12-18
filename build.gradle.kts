import net.thauvin.erik.gradle.semver.SemverConfig
import java.io.ByteArrayOutputStream
import net.thauvin.erik.gradle.semver.SemverIncrementBuildMetaTask
import net.thauvin.erik.gradle.semver.SemverIncrementTask
import net.thauvin.erik.gradle.semver.semver
import java.io.OutputStream

plugins {
  base
  kotlin("jvm") version "1.3.11"
  java
  checkstyle
  distribution
}

val spekVersion = "2.0.0-rc.1"
val junitVersion = "5.3.2"

buildscript {
  repositories {
      mavenLocal()
  }
  dependencies {
      classpath("net.thauvin.erik.gradle:semver:0.9.9-SNAPSHOT")
  }
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "net.thauvin.erik.gradle.semver")
  apply(plugin = "distribution")

  group = "com.lewisjmorgan.harvesterdroid"

  configure<SemverConfig> {
    preReleasePrefixKey = "SNAPSHOT"
    saveAfterProjectEvaluate = false
  }

  repositories {
    jcenter()
    //maven("https://dl.bintray.com/spekframework/spek-dev")
  }

  // Dependencies used across all projects
  dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("io.reactivex.rxjava2:rxjava:2.2.4")
    compile("io.reactivex.rxjava2:rxkotlin:2.3.0")

    addHarvesterDroidTestDependencies(this)
  }
  
  tasks {
    withType<Jar> {
      baseName = "harvesterdroid"
      appendix = project.name
    }
    val incrementBuildMetaTask = withType<SemverIncrementBuildMetaTask> {
      //println("Current build metadata: ${this.buildMeta}")
      doFirst {
        buildMeta = acquireCurrentGitCommitHash(true)
      }
    }
    withType<SemverIncrementTask> {
      doFirst {
        version.buildMeta = ""
      }
    }
    register("distribute") {
      group = "distribution"
      description = "Creates a distributable build"
      dependsOn(incrementBuildMetaTask)
      finalizedBy("build", "assembleDist")
    }
    withType<Test> {
      maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
      useJUnitPlatform {
        includeEngines("spek2")
      }
    }
    withType<Checkstyle> {
      configFile = File(rootDir, "checkstyle.xml")
    }
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

fun addHarvesterDroidTestDependencies(scope: DependencyHandlerScope) {
  scope {
    // Testing Dependencies
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation ("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")  {
      exclude("org.jetbrains.kotlin")
    }
    testRuntimeOnly ("org.spekframework.spek2:spek-runner-junit5:$spekVersion") {
      exclude("org.jetbrains.kotlin")
    }

    testCompile("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testRuntime("org.junit.platform:junit-platform-launcher:1.3.2")
  }
}

fun acquireCurrentGitCommitHash(short: Boolean): String {
  val stdout = ByteArrayOutputStream()
  exec {
    if (short) {
      commandLine("git", "rev-parse", "--short", "HEAD")
    } else {
      commandLine("git", "rev-parse", "HEAD")
    }
    standardOutput = stdout as OutputStream?
  }
  return stdout.toString().trim()
}
