package com.lewisjmorgan.harvesterdroid.app2.viewmodel

import com.lewisjmorgan.harvesterdroid.app2.InventoryItem
import tornadofx.*

class InventoryItemModel(item: InventoryItem?): ItemViewModel<InventoryItem>(item) {
  val resource = bind(InventoryItem::resource)
  val amount = bind(InventoryItem::amount)
}
