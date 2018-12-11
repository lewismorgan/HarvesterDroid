plugins {
  kotlin("jvm")
  java
}

description = "JavaFX Application Pre-loader for HarvesterDroid"

dependencies {
  compile(kotlin("stdlib-jdk8"))

  // Use JUnit test framework
  testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}