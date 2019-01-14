package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CachedGalaxyListRepositoryTest: Spek({
  describe("CachedGalaxyListRepository") {
    val repository by memoized { CachedGalaxyListRepository() }
    val galaxy by memoized { Galaxy("1337", "Europe-Chimaera") }
    describe("adding a galaxy") {
      it("adds a galaxy") {
        repository.add(galaxy)
        // Test the side effects
        val subscriber = TestSubscriber<Galaxy>()
        repository.getAll().subscribe(subscriber)
        subscriber.assertValue(galaxy)
      }
    }
    describe("getting all the galaxies") {
      beforeEachTest { repository.add(galaxy) }
      it("emits the cached galaxies") {
        val subscriber = TestSubscriber<Galaxy>()
        repository.getAll().subscribe(subscriber)
        subscriber.assertValue(galaxy)
      }
    }
  }
})