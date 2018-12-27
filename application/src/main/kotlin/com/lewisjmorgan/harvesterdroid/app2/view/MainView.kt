package com.lewisjmorgan.harvesterdroid.app2.view

import tornadofx.*

class MainView: View("HarvesterDroid") {
  override val root = vbox {
    add(MainMenuView::class)
    borderpane {
      left(InventoryView::class)
      right(FiltersView::class)
      bottom(AvailableResourcesView::class)
    }
  }
}