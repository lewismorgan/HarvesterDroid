package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyListRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.test.assertTrue

class GalaxyListServiceTest : Spek({
  describe("GalaxyListService") {
    val galaxies by memoized  { listOf(createGalaxy(), createGalaxy(), createGalaxy(), createGalaxy("Europe-Chimaera", "1337")) }
    val downloaded by memoized { listOf(createGalaxy(), createGalaxy()) }
    val repository by memoized { mock<GalaxyListRepository> {
      on { getAll() } doReturn Flowable.fromIterable(galaxies)
    }}
    val tracker by memoized { mock<Tracker> {
      on { downloadGalaxies() } doReturn Flowable.fromIterable(downloaded)
    }}

    val service by memoized { GalaxyListService(repository, tracker) }

    describe("getting galaxies") {
      val subscriber by memoized { TestSubscriber<Galaxy>() }

      it("emits downloaded galaxies") {
        service.downloadGalaxies().subscribe(subscriber)
        subscriber.assertValueSet(downloaded)
      }

      it("emits repository galaxies") {
        service.getGalaxies().subscribe(subscriber)
        subscriber.assertValueSequence(galaxies)
      }
    }

    describe("downloading galaxies") {
      val subscriber by memoized { TestSubscriber<Galaxy>() }

      it("adds galaxies to repository") {
        service.downloadGalaxies().subscribe().dispose()
        service.getGalaxies().subscribe(subscriber)
        subscriber.assertValueSet(downloaded.plus(galaxies))
        subscriber.assertComplete()
      }
      it("sets updated to true") {
        service.downloadGalaxies().subscribe().dispose()
        assertTrue(service.hasUpdatedGalaxies())
      }
    }

    describe("IO Operations") {
      val dataFactory by memoized { DataFactory() }
      describe("saving galaxies") {
        val subscriber by memoized { TestSubscriber<OutputStream>() }
        it("saves galaxies to an OutputStream") {
          service.save(ByteArrayOutputStream(0), dataFactory, MappingType.JSON).toFlowable().subscribe(subscriber)
          subscriber.assertValue { it is ByteArrayOutputStream && it.toByteArray().isNotEmpty() }
        }
      }
      describe("loading galaxies") {
        val subscriber by memoized { TestSubscriber<Galaxy>() }
        val stream by memoized { dataFactory.serialize(ByteArrayOutputStream(), galaxies, MappingType.BSON) as ByteArrayOutputStream }
        it("loads from an InputStream") {
          service.load(ByteArrayInputStream(stream.toByteArray()), dataFactory, MappingType.BSON)
            .subscribe(subscriber)
          subscriber.assertValueSet(galaxies)
          subscriber.assertComplete()
        }
      }
    }
  }
})

private val random by lazy { Random(1337L) }
fun createGalaxy(): Galaxy {
  return createGalaxy("Test", random.nextInt(500).toString())
}
fun createGalaxy(name: String, id: String): Galaxy {
  return Galaxy(id, name)
}