package com.lewisjmorgan.harvesterdroid.app2.viewmodel

import com.lewisjmorgan.harvesterdroid.api.service.IInventoryService
import com.lewisjmorgan.harvesterdroid.api.service.InventoryService
import com.lewisjmorgan.harvesterdroid.app2.events.AppStateSavedEvent
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEvent
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEventType
import com.lewisjmorgan.harvesterdroid.app2.kdi
import com.lewisjmorgan.harvesterdroid.app2.provider.InventoryFileProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import tornadofx.*
import java.io.OutputStream

// TODO Unit Testing for InventoryController's public methods

class InventoryController : Controller() {
  private val service: IInventoryService by kdi()
  private val provider: InventoryFileProvider by kdi()

  init {
    subscribe<AppStateSavedEvent> {
      onAppStateSaved()
    }

    subscribe<InventoryItemEvent> {
      when(it.type) {
        InventoryItemEventType.ADD -> addItem(it.item)
        InventoryItemEventType.REMOVE -> removeItem(it.item)
      }
    }
  }

  private fun onAppStateSaved() {
    saveToProvider().subscribeOn(Schedulers.io()).subscribe { _, _ -> print("Saved.") }
  }

  private fun saveToProvider(): Single<OutputStream> {
    return provider.getInventoryFile().flatMap { service.saveInventory(it.outputStream()) }.doAfterSuccess { it.close() }
  }

  fun addItem(item: InventoryItemModel): Single<Boolean> {
    return service.addItem(item.item)
  }

  fun removeItem(item: InventoryItemModel): Single<Boolean> {
    return service.removeItem(item.item)
  }
}
