plugins {
  base
  kotlin("jvm") version "1.3.11"
  java
  idea
  checkstyle
}

allprojects {
  group = "com.lewisjmorgan.harvesterdroid"
  version = "2.0.0-SNAPSHOT"

  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  repositories {
    jcenter()
  }

  // Dependencies used across all projects
  dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:1.7.25")

    // Use JUnit test framework
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
  }

  java.sourceCompatibility = JavaVersion.VERSION_1_8
  java.targetCompatibility = JavaVersion.VERSION_1_8

  tasks.withType<Jar> {
    baseName = "harvesterdroid"
    appendix = project.name
  }

  tasks.withType<Checkstyle> {
    configFile = File(rootDir, "checkstyle.xml")
  }
}

dependencies {
  // Make the root project archives configuration depend on every sub project
  subprojects.forEach {
    archives(it)
  }
}
