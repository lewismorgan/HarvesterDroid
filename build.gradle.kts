plugins {
  base
  kotlin("jvm") version "1.3.11"
  java
  checkstyle
}

val spekVersion = "2.0.0-rc.1"
val junitVersion = "5.3.2"

allprojects {
  group = "com.lewisjmorgan.harvesterdroid"
  version = "2.0.0-SNAPSHOT"

  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.jvm")

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

    addTestDependencies(this)
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

  tasks.withType<Test> {
    useJUnitPlatform {
      includeEngines("spek2")
    }
  }
}

dependencies {
  // Make the root project archives configuration depend on every sub project
  subprojects.forEach {
    archives(it)
  }
}

fun addTestDependencies(scope: DependencyHandlerScope) {
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
