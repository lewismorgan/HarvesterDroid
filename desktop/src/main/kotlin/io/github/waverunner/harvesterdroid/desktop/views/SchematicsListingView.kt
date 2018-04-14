package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

/**
 * Created by lewis on 4/14/18.
 */
class SchematicsListingView : View("Schematics") {
  override val root = titledpane(title) {
    listview<String> {
      items.add("Basic")
      items.add("Tools")
      items.add("Crafting Tools")
    }
  }
}