import java.io.ByteArrayOutputStream
import net.thauvin.erik.gradle.semver.SemverIncrementBuildMetaTask
import net.thauvin.erik.gradle.semver.SemverIncrementTask

plugins {
  base
  kotlin("jvm") version "1.3.11"
  java
  checkstyle
  id("net.thauvin.erik.gradle.semver") version "0.9.8-beta"
}

val spekVersion = "2.0.0-rc.1"
val junitVersion = "5.3.2"

allprojects {
  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "net.thauvin.erik.gradle.semver")

  java {
    this.sourceCompatibility = JavaVersion.VERSION_1_8
    this.sourceCompatibility = JavaVersion.VERSION_1_8
  }

  group = "com.lewisjmorgan.harvesterdroid"

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

  java.sourceCompatibility = JavaVersion.VERSION_1_8
  java.targetCompatibility = JavaVersion.VERSION_1_8

  tasks {
    val incrementBuildMetaTask = withType<SemverIncrementBuildMetaTask> {
      //println("Current build metadata: ${this.buildMeta}")
      doFirst {
        buildMeta = acquireCurrentGitCommitHash(true)
      }
    }

    withType<Jar> {
      baseName = "harvesterdroid"
      appendix = project.name
      // Only want the jar's to increment build meta since that's what the user will be running.
      dependsOn(incrementBuildMetaTask)
      doLast {
        println("VERSION: ${project.version}")
      }
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
    commandLine("git", "rev-parse", if (short) "--short" else null, "HEAD")
    standardOutput = stdout
  }
  return stdout.toString().trim()
}