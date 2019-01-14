package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyResourceRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.time.Instant
import java.util.*
import kotlin.random.Random
import kotlin.test.assertTrue

class GalaxyResourceServiceTest: Spek({
  describe("GalaxyResourceService") {
    val expectedResources by memoized { listOf(createGalaxyResource(), createGalaxyResource("Grumps"), createGalaxyResource("Grooly")) }
    val downloadedResources by memoized { listOf(createGalaxyResource("Grumps"), createGalaxyResource("Grooly"), createGalaxyResource())}
    val repository by memoized { mock<GalaxyResourceRepository> {
      on { getAll() } doReturn Flowable.fromIterable(expectedResources)
      on { get(resource = "Testing") } doReturn Single.just(expectedResources[0])
    }}
    val tracker by memoized { mock<Tracker> {
      on { downloadGalaxyResources("") } doReturn Flowable.fromIterable(downloadedResources)
    }}
    val service by memoized { GalaxyResourceService(repository, tracker) }
    val resourceSubscriber by memoized { TestSubscriber<GalaxyResource>() }

    describe("getting resources") {
      it("returns galaxy resources") {
        service.getResources().subscribe(resourceSubscriber)
        resourceSubscriber.assertValueSequence(expectedResources)
      }
    }
    describe("getting current resources") {
      it("returns only active resources") {
        val active = expectedResources.filter { it.isSpawned() }
        service.getActiveResources().subscribe(resourceSubscriber)
        resourceSubscriber.assertValueSet(active)
      }
    }
    describe("downloading latest galaxy resources") {
      val downloaded by memoized  { service.downloadLatestResources() }
      it("returns downloaded resources") {
        downloaded.subscribe(resourceSubscriber)
        resourceSubscriber.assertValueSet(downloadedResources)
      }
      it("sets updated to true") {
        downloaded.subscribe().dispose()
        assertTrue { service.hasUpdatedResources() }
      }
    }
    describe("IO Operations") {
      val dataFactory by memoized { DataFactory() }
      describe("saving") {
        val subscriber by memoized { TestSubscriber<OutputStream>() }
        it("serializes all the resources") {
          service.save(ByteArrayOutputStream(), dataFactory, MappingType.BSON)
            .toFlowable()
            .subscribe(subscriber)
          subscriber.assertComplete()
        }
      }
      describe("loading") {
        val resourceStream by memoized { dataFactory.serialize(ByteArrayOutputStream(), expectedResources, MappingType.BSON) as ByteArrayOutputStream }
        it("loads all resources"){
          service.load(ByteArrayInputStream(resourceStream.toByteArray()), dataFactory, MappingType.BSON)
            .subscribe(resourceSubscriber)
          resourceSubscriber.assertValueSequence(expectedResources)
          resourceSubscriber.assertComplete()
        }
      }
    }
  }
})

fun createGalaxyResource(): GalaxyResource {
  return createGalaxyResource("Testing")
}

private val random by lazy { Random(1337L) }
fun createGalaxyResource(name: String): GalaxyResource {
  val resource = GalaxyResource()
  resource.name = name
  if (random.nextInt(10) <= 5) resource.spawnDate = Date.from(Instant.now()) else resource.despawnDate = Date.from(Instant.now())
  return resource
}
