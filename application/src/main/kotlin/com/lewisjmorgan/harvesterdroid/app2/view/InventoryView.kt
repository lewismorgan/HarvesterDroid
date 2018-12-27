package com.lewisjmorgan.harvesterdroid.app2.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class InventoryView: View() {
  // TODO Inventory View Functionality
  override val root = vbox {
    label("Inventory")
    tableview(listOf(InventoryItem("smetho", 1337)).observable()) {
      column("Name", InventoryItem::name)
      column("Amount", InventoryItem::count)
    }
  }
}

@Suppress("HasPlatformType", "MemberVisibilityCanBePrivate")
class InventoryItem {
  constructor(name: String, count: Int) {
    this.count = count
    this.name = name
  }
  val countProperty = SimpleIntegerProperty()
  var count by countProperty
  val nameProperty = SimpleStringProperty()
  var name by nameProperty
}