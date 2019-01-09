package com.lewisjmorgan.harvesterdroid.app2.events

import com.lewisjmorgan.harvesterdroid.app2.InventoryItem
import tornadofx.*

class AddInventoryItemEvent(val item: InventoryItem): FXEvent()
class UpdateInventoryItemEvent(val item: InventoryItem): FXEvent()