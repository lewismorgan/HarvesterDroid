package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

class InventoryView : View("Inventory") {
  override val root = anchorpane {
    titledpane(title, collapsible = false) {
      // List view will consist of
      vbox {
        listview<String> {
          items.add("menoef")
          items.add("iwiv")
        }
        buttonbar {
          button("Add")
          button("Remove")
        }
      }
      anchorpaneConstraints {
        bottomAnchor = 0.0; topAnchor = 0.0; leftAnchor = 0.0; rightAnchor = 0.0
      }
    }
  }
}
