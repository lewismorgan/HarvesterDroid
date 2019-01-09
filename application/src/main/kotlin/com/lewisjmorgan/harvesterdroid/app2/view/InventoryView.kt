package com.lewisjmorgan.harvesterdroid.app2.view

import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryViewModel
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.transform.Transform
import tornadofx.*

class InventoryView: View("Inventory") {
  private val model: InventoryViewModel by inject()
  override val root = vbox {
    label("Inventory")
    tableview(model.inventory) {
      columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
      column("Resource", InventoryItemModel::resource)
      column("Amount", InventoryItemModel::amount)
    }
    buttonbar {
      transforms.add(Transform.translate(10.0, 0.0))
      button("Add") {

      }
      button("Remove")
    }
  }
}
