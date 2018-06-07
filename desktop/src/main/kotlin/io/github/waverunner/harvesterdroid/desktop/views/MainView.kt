package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

/**
 * Created by lewis on 4/14/18.
 */
class MainView : View("Harvest Droid") {
  override val root = vbox {
    prefHeight = 800.0
    prefWidth = 600.0
    add(MainMenuBarView::class)
    vbox {
      isFillWidth = true
      hbox {
        add(InventoryView::class)
        add(SchematicsListingView::class)
      }
      add(DiscoveredResourcesView::class)
    }
  }
}