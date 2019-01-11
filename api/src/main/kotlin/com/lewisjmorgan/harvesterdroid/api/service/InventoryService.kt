package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.repository.CachedInventoryRepository
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream

class InventoryService(val repository: CachedInventoryRepository) {
  private val dataFactory by lazy { DataFactory() }

  fun saveInventory(destinationJson: OutputStream): Single<OutputStream> {
    // After saving, map the OutputStream to a File. If the file is created, then return the result.
    return repository.save(destinationJson, dataFactory, MappingType.JSON)
  }

  fun loadInventory(storedJsonData: InputStream): Flowable<InventoryItem> {
    return repository.load(storedJsonData, dataFactory, MappingType.JSON)
  }
}