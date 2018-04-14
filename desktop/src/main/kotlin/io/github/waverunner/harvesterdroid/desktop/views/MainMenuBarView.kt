package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

class MainMenuBarView : View() {
  override val root = menubar {
    menu("File") {
      item("Switch Galaxy") // TODO Galaxy switch
      item("Save") // TODO Saving state
      separator()
      item("Quit") // TODO Quit
    }
    menu("Import") {
      item("Resources") // TODO Resource import
      item("Schematics") // TODO Schematics
    }
    menu("Help") {
      item("About") // TODO About action
    }
  }
}
