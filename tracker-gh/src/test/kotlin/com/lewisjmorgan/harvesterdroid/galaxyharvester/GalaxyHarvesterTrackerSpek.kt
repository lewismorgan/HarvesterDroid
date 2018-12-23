@file:Suppress("SpellCheckingInspection")

package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.todo

class GalaxyHarvesterTrackerSpek: Spek({
  describe("GalaxyHarvesterTracker") {
    val tracker by memoized { GalaxyHarvesterTracker() }

    context("downloading galaxy list") {
      it("it emits parsed galaxies") {
        val testSubscriber = TestSubscriber<Galaxy>()
        tracker.downloadGalaxies().subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
      }
    }
    context("downloading a resource") {
      val testSubscriber by memoized { TestSubscriber<GalaxyResource>() }
      it("emits a valid resource") {
        tracker.downloadGalaxyResource("48", "desrio").toFlowable()
          .subscribe(testSubscriber)
        testSubscriber.assertValue { it.name == "desrio" }
        testSubscriber.assertComplete()
      }
      todo {
        it("emits an error for an invalid resource") {
          tracker.downloadGalaxyResource("48", "a").toFlowable()
            .subscribe(testSubscriber)
          testSubscriber.assertError { true }
        }
      }
    }
    context("downloading a list of recent resources") {
      val testSubscriber by memoized { TestSubscriber<GalaxyResource>() }
      it("emits a flow of galaxy resources") {
        tracker.downloadGalaxyResources("48").subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
      }
    }
  }
})
