package com.lewisjmorgan.harvesterdroid.app2.view

import tornadofx.*

class AvailableResourcesView: View("My View") {
  // TODO Available Resources View Functionality

  override val root = vbox {
    label("Best Resources")
    hbox {
      checkbox("Show Only Available")
    }
    listview(observableList("smetho", "argusdfkj", "copper", "steel"))
  }
}
