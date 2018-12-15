
val spekVersion = "2.0.0-rc.1"
val kotlinVersion = "1.3.11"

repositories {
  maven("https://dl.bintray.com/spekframework/spek")
}

dependencies {
  compile(project(":api"))

  testImplementation(kotlin("reflect", kotlinVersion))
  testImplementation(kotlin("test-junit", kotlinVersion))
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
  testImplementation ("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")  {
    exclude("org.jetbrains.kotlin")
  }
  testRuntimeOnly ("org.spekframework.spek2:spek-runner-junit5:$spekVersion") {
    exclude("org.junit.platform")
    exclude("org.jetbrains.kotlin")
  }
}

tasks.withType<Test> {
  useJUnitPlatform {
    includeEngines("spek2")
  }
}
