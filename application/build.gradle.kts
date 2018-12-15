//import groovy.lang.Closure

plugins {
  application
}

val mainClass = "com.lewisjmorgan.harvesterdroid.Launcher"
description = "Manage your resources across a number of Star Wars Galaxies resource trackers"

dependencies {
  compile(project(":api"))
  compile(project(":loader"))
  compile(project(":tracker-gh"))
  compile("de.saxsys:mvvmfx:1.6.0")
  compile("de.saxsys:mvvmfx-easydi:1.6.0")
  compile("org.controlsfx:controlsfx:8.40.13")
  compile("javax.persistence:persistence-api:1.0.2")
  compile("com.vladsch.flexmark:flexmark:0.22.22")
  compile("org.mongodb:mongodb-driver:3.5.0")
  compile("org.apache.logging.log4j:log4j-api:2.8.2")
  compile("org.apache.logging.log4j:log4j-core:2.8.2")
  compile("org.apache.logging.log4j:log4j-slf4j-impl:2.8.2")

  // Use JUnit test framework
  testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  applicationName = "harvesterdroid"
  mainClassName = mainClass
}

distributions {
  main {
    baseName = "harvesterdroid"
  }
}

tasks.withType<Jar> {
  archiveName = "harvesterdroid-${project.version}.jar"
//  manifest {
//    attributes(
//      "Main-Class" to mainClass,
//      "implementation-title" to "HarvesterDroid",
//      "implementation-version" to project.version,
//      "implementation-vendor" to "Waverunner",
//      "JavaFX-Preloader-Class" to "com.lewisjmorgan.harvesterdroid.LauncherPreloader",
//      "JavaFX-Fallback-Class" to "com.javafx.main.NoJavaFXFallback",
//      "JavaFX-Application-Class" to mainClass
//    )
//  }
}
