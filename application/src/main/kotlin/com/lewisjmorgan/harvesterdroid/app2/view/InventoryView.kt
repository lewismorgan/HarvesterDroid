package com.lewisjmorgan.harvesterdroid.app2.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.additions
import com.github.thomasnield.rxkotlinfx.events
import com.github.thomasnield.rxkotlinfx.onChangedObservable
import com.github.thomasnield.rxkotlinfx.removals
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryController
import javafx.event.ActionEvent
import javafx.scene.control.TableView
import javafx.scene.transform.Transform
import tornadofx.*

class InventoryView: View("Inventory") {
  private val controller: InventoryController by inject()

  private val filteredInventory = SortedFilteredList<InventoryItemModel>()
  override val root = vbox {
    label("Inventory")

    filteredInventory.items.additions().flatMapSingle { added -> controller.addItem(added).map { if (it) added else null } }
      .subscribe {
        println("Added $it")
      }

    filteredInventory.items.removals().flatMapSingle { removed -> controller.removeItem(removed).map { if (it) removed else null } }
      .subscribe {
        println("Removed: $it")
      }

    tableview(filteredInventory) {
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
          filteredInventory.add(inventoryItem)
        }
      }
      button("Remove") {
        // TODO Remove selected inventory item
        actionEvents().subscribe {
          filteredInventory.remove(inventoryItem)
        }
      }
    }
  }
}
