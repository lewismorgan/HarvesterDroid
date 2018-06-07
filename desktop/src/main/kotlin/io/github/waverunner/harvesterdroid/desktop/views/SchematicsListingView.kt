package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

/**
 * Created by lewis on 4/14/18.
 */
class SchematicsListingView : View("Schematics") {
  override val root = anchorpane {
    titledpane(title, collapsible = false) {
      vbox {
        listview<String> {
          items.add("Basic")
          items.add("Tools")
          items.add("Crafting Tools")
        }
        buttonbar {
          button("Add")
          button("Remove")
          button("Edit")
        }
      }
      anchorpaneConstraints { leftAnchor = 0.0; rightAnchor = 0.0; bottomAnchor = 0.0; topAnchor = 0.0 }
    }
  }
}