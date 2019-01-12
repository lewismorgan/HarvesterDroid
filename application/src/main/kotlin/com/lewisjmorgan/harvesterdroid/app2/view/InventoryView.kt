package com.lewisjmorgan.harvesterdroid.app2.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import com.lewisjmorgan.harvesterdroid.app2.controller.InventoryController
import javafx.scene.control.TableView
import javafx.scene.transform.Transform
import tornadofx.*

class InventoryView: View("Inventory") {
  private val controller: InventoryController by inject()
  override val root = vbox {
    label("Inventory")
    tableview(controller.filteredInventory) {
      columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
      column("Resource", InventoryItemModel::resource)
      column("Amount", InventoryItemModel::amount)
    }
    buttonbar {
      val inventoryItem = InventoryItemModel(InventoryItem("TestItem", 1337))
      transforms.add(Transform.translate(10.0, 0.0))

      button("Add") {
        // TODO Display Add Inventory Item dialog
        actionEvents().subscribe {
          controller.addItem(inventoryItem)
        }
      }
      button("Remove") {
        // TODO Remove selected inventory item
        actionEvents().subscribe {
          controller.removeItem(inventoryItem)
        }
      }
      button("Save") {
        actionEvents().subscribe {
          controller.onAppStateSaved()
        }
      }
    }
  }
}
