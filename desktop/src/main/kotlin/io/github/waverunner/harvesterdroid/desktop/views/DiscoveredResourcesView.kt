package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

/**
 * Created by lewis on 4/14/18.
 */
class DiscoveredResourcesView : View("Discovered Resources") {
  override val root = anchorpane {
    titledpane(title, collapsible = false) {
      vbox {
        checkbox("Show Only Available Resources", true.toProperty())
        listview<String> {
          items.add("menoef")
          items.add("iwiv")
        }
      }
    }
    anchorpaneConstraints { bottomAnchor = 0.0; topAnchor = 0.0; leftAnchor = 0.0; rightAnchor = 0.0 }
  }
}