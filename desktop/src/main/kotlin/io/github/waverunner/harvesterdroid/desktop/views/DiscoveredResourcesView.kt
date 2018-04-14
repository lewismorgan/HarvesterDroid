package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.*

/**
 * Created by lewis on 4/14/18.
 */
class DiscoveredResourcesView : View("Discovered Resources") {
  override val root = titledpane(title) {
    listview<String> {
      items.add("menoef")
      items.add("iwiv")
    }
  }
}