package com.lewisjmorgan.harvesterdroid.app2.view

import tornadofx.*
import javafx.scene.control.MenuBar

class MainMenuView: View() {
  override val root = menubar {
    menu("File") {
      item("Save")
      item("Quit")
    }
    menu("Tracker") {
      item("Download Resources")
      item("Change")
      item("About")
    }
    menu("Import") {
      item("Resources")
      item("Filters")
    }
    menu("Help") {
      item("About")
    }
  }
}
