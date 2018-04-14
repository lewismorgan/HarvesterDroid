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
    gridpane {
      
      row {
        anchorpane {
          gridpaneConstraints {
            columnIndex = 0
          }
          add(InventoryView::class)
        }
      }

      row {
        gridpaneConstraints {
          columnIndex = 1
        }
        add(SchematicsListingView::class)
      }

      row {
        anchorpane {
          useMaxWidth = true
          gridpaneConstraints {
            columnSpan = 2
          }
          add(DiscoveredResourcesView::class)
        }
      }
    }
  }
}