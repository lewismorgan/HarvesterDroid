package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

class InventoryView : View("Inventory") {
  override val root = titledpane {
    // List view will consist of
    listview<String> {
      items.add("menoef")
      items.add("iwiv")
    }
  }
}
