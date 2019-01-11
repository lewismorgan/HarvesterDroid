package com.lewisjmorgan.harvesterdroid.app2.events

import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import tornadofx.*

class InventoryItemEvent(val item: InventoryItemModel, val type: InventoryItemEventType): FXEvent()

enum class InventoryItemEventType {
  ADD, REMOVE
}