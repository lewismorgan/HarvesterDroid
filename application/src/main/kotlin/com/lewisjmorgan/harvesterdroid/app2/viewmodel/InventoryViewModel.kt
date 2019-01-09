package com.lewisjmorgan.harvesterdroid.app2.viewmodel

import com.lewisjmorgan.harvesterdroid.app2.events.AddInventoryItemEvent
import com.lewisjmorgan.harvesterdroid.app2.events.UpdateInventoryItemEvent
import tornadofx.*

class InventoryViewModel : ViewModel() {
  val inventory = arrayListOf<InventoryItemModel>().observable()

  init {
    subscribe<AddInventoryItemEvent> {
      inventory.add(InventoryItemModel(it.item))
    }
    subscribe<UpdateInventoryItemEvent> {
      // TODO Update Inventory from event
    }
  }
}
