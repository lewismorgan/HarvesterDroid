package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import io.reactivex.Flowable
import io.reactivex.Single

interface InventoryRepository {
  fun getAll(): Flowable<InventoryItem>
  fun get(resource: String): Single<InventoryItem>
  fun add(item: InventoryItem): Single<Boolean>
  fun remove(item: InventoryItem): Single<Boolean>
  fun remove(resource: String): Single<Boolean>
  fun exists(resource: String): Single<Boolean>
}