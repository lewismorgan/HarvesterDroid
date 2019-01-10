package com.lewisjmorgan.harvesterdroid.app2.events

import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import tornadofx.*

class AddInventoryItemEvent(val item: InventoryItem): FXEvent()
class UpdateInventoryItemEvent(val item: InventoryItem): FXEvent()