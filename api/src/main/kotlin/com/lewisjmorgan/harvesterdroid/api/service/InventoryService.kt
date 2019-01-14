package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.repository.InventoryRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.io.InputStream
import java.io.OutputStream

interface IInventoryService {
  val selectedInventoryItem: PublishSubject<InventoryItem>
  fun saveInventory(destinationJson: OutputStream): Single<OutputStream>
  fun loadInventory(storedJsonData: InputStream): Flowable<InventoryItem>
  fun addItem(item: InventoryItem): Single<Boolean>
  fun removeItem(item: InventoryItem): Single<Boolean>
  fun selectItem(item: InventoryItem)
  fun getInventory(): Flowable<InventoryItem>
}

class InventoryService(private val repository: InventoryRepository): IInventoryService {
  override val selectedInventoryItem: PublishSubject<InventoryItem> = PublishSubject.create()
  private val dataFactory by lazy { DataFactory() }

  override fun selectItem(item: InventoryItem) {
    selectedInventoryItem.onNext(item)
  }

  override fun removeItem(item: InventoryItem): Single<Boolean> {
    return repository.remove(item.resource)
  }

  override fun addItem(item: InventoryItem): Single<Boolean> {
    return repository.add(item)
  }

  override fun getInventory(): Flowable<InventoryItem> {
    return repository.getAll()
  }

  override fun saveInventory(destinationJson: OutputStream): Single<OutputStream> {
    return repository.getAll().toList().map { list ->
      list.forEach { println("InventoryITem: $it") }
      dataFactory.serialize(destinationJson, list, MappingType.JSON) }
  }

  override fun loadInventory(storedJsonData: InputStream): Flowable<InventoryItem> {
    return Observable.create<InventoryItem> { emitter ->
      try {
        val result = dataFactory.deserialize(storedJsonData, MappingType.JSON) as List<InventoryItem>
        result.forEach {
          emitter.onNext(it)
        }
        emitter.onComplete()
      } catch (t: Throwable) {
        emitter.onError(t)
      }

    }.toFlowable(BackpressureStrategy.BUFFER)
  }
}