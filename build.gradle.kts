plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
  base
  kotlin("jvm") version "1.3.11" apply false
  java
  idea
  checkstyle
}

allprojects {
  group = "com.lewisjmorgan.harvesterdroid"
  version = "2.0.0-SNAPSHOT"

  apply(plugin = "java")

  repositories {
    jcenter()
  }

  tasks.withType<Checkstyle> {
    configFile = File(rootDir, "checkstyle.xml")
  }

  java.sourceCompatibility = JavaVersion.VERSION_1_8
  java.targetCompatibility = JavaVersion.VERSION_1_8

  tasks.withType<Jar> {
    baseName = "harvesterdroid"
    appendix = project.name
  }
}

dependencies {
  // Make the root project archives configuration depend on every sub project
  subprojects.forEach {
    archives(it)
  }
}
