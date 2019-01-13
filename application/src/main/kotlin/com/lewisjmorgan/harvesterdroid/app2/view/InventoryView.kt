package com.lewisjmorgan.harvesterdroid.app2.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import com.lewisjmorgan.harvesterdroid.app2.controller.InventoryController
import io.reactivex.Observable
import io.reactivex.rxjavafx.observables.JavaFxObservable
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.transform.Transform
import javafx.stage.StageStyle
import tornadofx.*
import tornadofx.controlsfx.loginDialog
import tornadofx.controlsfx.progressDialog

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
