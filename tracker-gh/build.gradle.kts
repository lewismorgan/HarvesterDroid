plugins {
  kotlin("jvm")
  java
}

dependencies {
  compile(kotlin("stdlib-jdk8"))
  compile(project(":api"))

  // Use JUnit test framework
  testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}
