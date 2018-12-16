package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyListRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertTrue
import kotlin.test.todo

class GalaxyListServiceTest : Spek({
  describe("GalaxyListService") {
    val repoGalaxy = Galaxy("0", "Testing Galaxy")
    val trackerGalaxy = Galaxy("1337", "Overpowered")

    val repository by memoized { mock<GalaxyListRepository> {
      on { getAll() } doReturn Flowable.just(repoGalaxy)
    }}
    val tracker by memoized { mock<Tracker> {
      on { downloadGalaxies() } doReturn Flowable.just(trackerGalaxy)
    }}

    val service by memoized { GalaxyListService(repository, tracker) }

    describe("getting galaxies") {
      val subscriber by memoized { TestSubscriber<Galaxy>() }

      it("emits a repository galaxy") {
        val galaxies = service.getGalaxies()
        galaxies.subscribe(subscriber)
        subscriber.assertValue(repoGalaxy)
      }
    }

    describe("downloading galaxies") {
      val subscriber by memoized { TestSubscriber<Galaxy>() }

      it("emits tracker galaxies") {
        val downloaded = service.downloadGalaxies()
        downloaded.subscribe(subscriber)
        subscriber.assertValue(trackerGalaxy)
        subscriber.assertComplete()
      }
      it("adds galaxies to repository") {
        val galaxies = service.getGalaxies()
        galaxies.subscribe(subscriber)
        subscriber.assertValue(repoGalaxy)
        subscriber.assertComplete()
      }
      it("sets updated to true") {
        service.downloadGalaxies().subscribe().dispose()
        assertTrue(service.hasUpdatedGalaxies())
      }
    }
  }
})