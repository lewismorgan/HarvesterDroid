package com.lewisjmorgan.harvesterdroid.app2.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.lewisjmorgan.harvesterdroid.app2.controller.InventoryController
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
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
      transforms.add(Transform.translate(10.0, 0.0))

      button("Add") {
        actionEvents().flatMap { inventoryItemEditor() }
          .filter { !controller.filteredInventory.contains(it) }
          .subscribe({
            controller.addItem(it)
          }, {
            println("Error when creating a new inventory item!")
            it.printStackTrace()
          })
      }
      button("Remove") {
        // TODO Remove selected inventory item
        actionEvents().subscribe {
//          controller.removeItem(inventoryItem)
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
