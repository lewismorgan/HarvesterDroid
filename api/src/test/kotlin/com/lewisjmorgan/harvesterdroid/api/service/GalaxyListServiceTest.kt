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
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
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

      it("adds galaxies to repository") {
        val galaxies = service.getGalaxies()
        galaxies.subscribe(subscriber)
        subscriber.assertValue(repoGalaxy)
        subscriber.assertComplete()
      }
      it("sets updated to true") {
        service.getGalaxies().subscribe().dispose()
        assertTrue(service.hasUpdatedGalaxies())
      }
    }

    describe("saving galaxies") {
      it("saves galaxy to an OutputStream") {
        val result = service.saveGalaxies(ByteArrayOutputStream(0)) as ByteArrayOutputStream
        assertTrue(result.toByteArray().isNotEmpty())
      }
    }
    describe("loading galaxies") {
      it("loads from an InputStream") {
        val testSubscriber = TestSubscriber<Galaxy>()
        val stream = service.saveGalaxies(ByteArrayOutputStream(0)) as ByteArrayOutputStream
        service.loadGalaxies(ByteArrayInputStream(stream.toByteArray())).subscribe(testSubscriber)
        testSubscriber.assertValue { it.id == repoGalaxy.id && it.name == repoGalaxy.name }
        testSubscriber.assertComplete()
      }
    }
  }
})