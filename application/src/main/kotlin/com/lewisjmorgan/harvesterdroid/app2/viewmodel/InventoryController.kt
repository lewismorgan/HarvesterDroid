package com.lewisjmorgan.harvesterdroid.app2.viewmodel

import com.lewisjmorgan.harvesterdroid.api.repository.CachedInventoryRepository
import com.lewisjmorgan.harvesterdroid.api.service.InventoryService
import com.lewisjmorgan.harvesterdroid.app2.HarvesterDroidProvider
import com.lewisjmorgan.harvesterdroid.app2.events.AppStateSavedEvent
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEvent
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEventType
import com.lewisjmorgan.harvesterdroid.app2.provider.InventoryFileProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import tornadofx.*
import java.io.OutputStream

// TODO Unit Testing for InventoryController's public methods

class InventoryController : Controller() {
  // TODO Inject InventoryService using Guice
  private val service: InventoryService = InventoryService(CachedInventoryRepository())
  // TODO Inject using Guice
  private val provider: InventoryFileProvider = HarvesterDroidProvider()

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
    saveToFile().subscribeOn(Schedulers.io()).subscribe { _, _ -> print("Saved to file :)") }
  }

  private fun saveToFile(): Single<OutputStream> {
    return provider.getInventoryFile().flatMap { service.saveInventory(it.outputStream()) }.doAfterSuccess { it.close() }
  }

  fun addItem(item: InventoryItemModel): Single<Boolean> {
    return service.repository.add(item.item)
  }

  fun removeItem(item: InventoryItemModel): Single<Boolean> {
    return service.repository.remove(item.resource.toString())
  }

  fun editItem(item: InventoryItemModel): Single<InventoryItemModel> {
    TODO("Return result of edited inventory item")
  }
}
