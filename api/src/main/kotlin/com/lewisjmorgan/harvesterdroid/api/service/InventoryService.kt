package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.api.repository.InventoryRepository
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream

interface IInventoryService {
  fun saveInventory(destinationJson: OutputStream): Single<OutputStream>
  fun loadInventory(storedJsonData: InputStream): Flowable<InventoryItem>
  fun addItem(item: InventoryItem): Single<Boolean>
  fun removeItem(item: InventoryItem): Single<Boolean>
}

class InventoryService(private val repository: InventoryRepository): IInventoryService {
  private val dataFactory by lazy { DataFactory() }

  override fun removeItem(item: InventoryItem): Single<Boolean> {
    return repository.remove(item.resource)
  }

  override fun addItem(item: InventoryItem): Single<Boolean> {
    return repository.add(item)
  }

  override fun saveInventory(destinationJson: OutputStream): Single<OutputStream> {
    // After saving, map the OutputStream to a File. If the file is created, then return the result.
    TODO("return repository.save(destinationJson, dataFactory, MappingType.JSON)")
  }

  override fun loadInventory(storedJsonData: InputStream): Flowable<InventoryItem> {
    TODO("return repository.load(storedJsonData, dataFactory, MappingType.JSON)")
  }
}