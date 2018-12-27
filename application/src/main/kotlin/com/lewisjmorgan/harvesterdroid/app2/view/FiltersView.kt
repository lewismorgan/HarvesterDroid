package com.lewisjmorgan.harvesterdroid.app2.view

import tornadofx.*

class FiltersView: View() {
  // TODO Filters View Functionality
  override val root = vbox {
    label("Filters")
    listview(observableList("OQ 1000"))
  }
}