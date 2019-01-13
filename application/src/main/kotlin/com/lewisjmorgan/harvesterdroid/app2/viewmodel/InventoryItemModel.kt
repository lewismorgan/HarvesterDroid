package com.lewisjmorgan.harvesterdroid.app2.viewmodel

import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import tornadofx.*

class InventoryItemModel(item: InventoryItem?): ItemViewModel<InventoryItem>(item) {
  @Suppress("unused")
  constructor(): this(null)
  val resource = bind(InventoryItem::resource)
  val amount = bind(InventoryItem::amount)
}
