package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.InventoryItem
import io.reactivex.subscribers.TestSubscriber
import org.mockito.internal.util.reflection.FieldSetter
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CachedInventoryRepositoryTest: Spek({
  describe("CachedInventoryRepository") {
    val items = mutableListOf(InventoryItem("a", 1),
      InventoryItem("b", 2),
      InventoryItem("c", 3))

    val repository by memoized { createCachedRepository(items) }

    describe("getting") {
      val subscriber by memoized { TestSubscriber<InventoryItem>() }
      describe("getAll") {
        it("emits all inventory items") {
          repository.getAll().subscribe(subscriber)
          subscriber.assertValueSet(items)
          subscriber.assertValueCount(items.size)
        }
      }
      describe("get by resource name") {
        items.forEach { expected ->
          it("emits $expected from the name") {
            repository.get(expected.resource).toFlowable().subscribe(subscriber)
            subscriber.assertValue(expected)
          }
        }
      }
    }
    describe("add") {
      val toAdd by memoized { InventoryItem("adding", 1500) }
      val subscriber by memoized { TestSubscriber<Boolean>() }
      it("emits true when successfully added") {
        repository.add(toAdd).toFlowable().subscribe(subscriber)
        subscriber.assertValue(true)
      }
      it("emits false when it is not added if it exists already") {
        repository.add(items[0]).toFlowable().subscribe(subscriber)
        subscriber.assertValue(false)
      }
    }
    describe("removing") {
      val subscriber by memoized { TestSubscriber<Boolean>() }
      val toRemove by memoized { items[0] }
      describe("by resource") {
        it("emits true when the item is removed from the cache") {
          repository.remove(toRemove.resource).toFlowable().subscribe(subscriber)
          subscriber.assertValue(true)
        }
        it("emits false when the item is not removed from the cache") {
          repository.remove("resourceNotInCache").toFlowable().subscribe(subscriber)
          subscriber.assertValue(false)
        }
      }
      describe("by item") {
        it("emits true when the item is removed from the cache") {
          repository.remove(toRemove).toFlowable().subscribe(subscriber)
          subscriber.assertValue(true)
        }
        it("emits false when the item is not removed from the cache") {
          repository.remove(InventoryItem("itemNotInCache", 1337)).toFlowable().subscribe(subscriber)
          subscriber.assertValue(false)
        }
      }
    }
    describe("exists") {
      val subscriber by memoized { TestSubscriber<Boolean>() }
      it("emits true if it exists") {
        repository.exists(items[0].resource).toFlowable().subscribe(subscriber)
        subscriber.assertValue(true)
      }
      it("emits false if it does not exist") {
        repository.exists("resourceThatDoesn'tExistInCache").toFlowable().subscribe(subscriber)
        subscriber.assertValue(false)
      }
    }
  }
})

private fun createCachedRepository(items: List<InventoryItem>): CachedInventoryRepository {
  val repository = CachedInventoryRepository()
  FieldSetter.setField(repository, CachedInventoryRepository::class.java.getDeclaredField("cache"), items)
  return repository
}