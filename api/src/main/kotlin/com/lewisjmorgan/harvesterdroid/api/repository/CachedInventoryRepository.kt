package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable

class CachedInventoryRepository: CachedRepository<InventoryItem>(), InventoryRepository {
  override val cache: MutableList<InventoryItem> = mutableListOf()

  override fun getAll(): Flowable<InventoryItem> = Flowable.fromIterable(cache)

  override fun get(resource: String): Single<InventoryItem> {
    return cache.toObservable().filter { it.resource == resource }.singleOrError()
  }

  override fun add(item: InventoryItem): Single<Boolean> {
    return Single.create<Boolean> {
      if (cache.contains(item) || cache.find { inventoryItem -> inventoryItem.resource == item.resource } != null)
        it.onSuccess(false)
      else
        it.onSuccess(cache.add(item))
    }
  }

  override fun remove(item: InventoryItem): Single<Boolean> {
    return Single.create<Boolean> {
      it.onSuccess(cache.remove(item))
    }
  }

  override fun remove(resource: String): Single<Boolean> {
    return Single.create<Boolean> {
      val toRemove = cache.filter { inventoryItem -> inventoryItem.resource == resource }
      if (toRemove.size != 1)
        it.onSuccess(false)
      else it.onSuccess(cache.remove(toRemove.first()))
    }
  }

  override fun exists(resource: String): Single<Boolean> {
    return Single.create<Boolean> {
      it.onSuccess(cache.find { inventoryItem -> inventoryItem.resource == resource } != null)
    }
  }
}