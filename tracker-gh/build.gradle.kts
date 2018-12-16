
val spekVersion = "2.0.0-rc.1"

repositories {
  maven("https://dl.bintray.com/spekframework/spek")
}

dependencies {
  compile(project(":api"))
}
