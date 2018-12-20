import net.thauvin.erik.gradle.semver.SemverConfig
import java.io.ByteArrayOutputStream
import net.thauvin.erik.gradle.semver.SemverIncrementBuildMetaTask
import net.thauvin.erik.gradle.semver.SemverIncrementTask
import java.io.OutputStream

plugins {
  base
  kotlin("jvm") version "1.3.11"
  java
  checkstyle
  distribution
  id("nebula.release") version "9.1.2"
}

buildscript {
  repositories {
    mavenLocal()
    jcenter()
  }
  dependencies {
    classpath("net.thauvin.erik.gradle:semver:0.9.9-SNAPSHOT")
  }
}

repositories {
  jcenter()
}

val spekVersion = "2.0.0-rc.1"
val junitVersion = "5.3.2"

subprojects {
  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "distribution")
  apply(plugin = "nebula.release")

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
  
  tasks {
    withType<Jar> {
      baseName = "harvesterdroid"
      appendix = project.name
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
    register<Copy>("addRuntimeLibs") {
      group = "distribution"
      description = "Places the runtime libraries into buildDir/libs"
      into("$buildDir/libs")
      from(configurations.runtime)
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
