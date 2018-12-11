plugins {
    kotlin("jvm")
    java
}

description = "Core classes for HarvesterDroid"

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("javax.persistence:persistence-api:1.0.2")
    compile("de.undercouch:bson4jackson:2.7.0")
    compile("com.fasterxml.jackson.core:jackson-core:2.7.9")

    // Use JUnit test framework
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}
