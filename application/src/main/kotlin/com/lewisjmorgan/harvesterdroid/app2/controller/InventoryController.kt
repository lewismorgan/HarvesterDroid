package com.lewisjmorgan.harvesterdroid.app2.controller

import com.github.thomasnield.rxkotlinfx.additions
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.api.service.IInventoryService
import com.lewisjmorgan.harvesterdroid.app2.events.AppStateEvent
import com.lewisjmorgan.harvesterdroid.app2.events.AppStateEventType
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEvent
import com.lewisjmorgan.harvesterdroid.app2.events.InventoryItemEventType
import com.lewisjmorgan.harvesterdroid.app2.kdi
import com.lewisjmorgan.harvesterdroid.app2.provider.InventoryDataProvider
import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import io.reactivex.Observable
import io.reactivex.Single
import tornadofx.*
import java.io.OutputStream

// TODO Unit Testing for InventoryController's public methods

class InventoryController : Controller() {
  private val service: IInventoryService by kdi()
  private val provider: InventoryDataProvider by kdi()

  val filteredInventory = SortedFilteredList<InventoryItemModel>()

  init {
    subscribe<AppStateEvent> {
      when(it.type) {
        AppStateEventType.SAVE -> onAppStateSaved()
        AppStateEventType.LOAD -> onAppStateLoad()
      }
    }

    subscribe<InventoryItemEvent> {
      when(it.type) {
        InventoryItemEventType.ADD -> addItem(it.item)
        InventoryItemEventType.REMOVE -> removeItem(it.item)
      }
    }
  }

  private fun setupFilteredInventory() {
    filteredInventory.items.additions()
      .map { it.item }
      .concatMap { item -> service.addItem(item).toObservable() }
      .doOnError {
        println("setupFilteredInventory: An error occurred when adding to the filteredInventory!")
        it.printStackTrace()
      }
      .subscribe()
  }

  private fun onAppStateLoad() {
    provider.inventoryInputStream().flatMap {
      service.loadInventory(it)
        .onErrorReturnItem(InventoryItem("smetho", 1337))
        .map { item -> InventoryItemModel(item) }.toList()
    }.subscribe { list, _ ->
        list.forEach {
          addItem(it)
        }
      }
    setupFilteredInventory()
  }

  fun onAppStateSaved() {
    saveToProvider()
      .subscribe()
  }

  private fun saveToProvider(): Single<OutputStream> {
    return provider.inventoryOutputStream().flatMap { service.saveInventory(it) }
  }

  @Suppress("unused")
  fun selectedItem(): Observable<InventoryItem> {
    return service.selectedInventoryItem
  }

  fun addItem(item: InventoryItemModel) {
    if (!filteredInventory.contains(item))
      filteredInventory.add(item)
  }

  fun removeItem(item: InventoryItemModel) {
    filteredInventory.remove(item)
  }
}
