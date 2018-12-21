@file:Suppress("SpellCheckingInspection")

package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class GalaxyHarvesterTrackerSpek: Spek({
  describe("GalaxyHarvesterTracker") {
    context("downloading galaxy list") {
      val tracker by memoized { GalaxyHarvesterTracker() }
      it("it emits parsed galaxies") {
        val testSubscriber = TestSubscriber<Galaxy>()
        tracker.downloadGalaxies().subscribe(testSubscriber)
        assert(testSubscriber.valueCount() > 0)
      }
    }
    context("downloading a resource") {
      val tracker by memoized { GalaxyHarvesterTracker() }
      val testSubscriber by memoized { TestSubscriber<GalaxyResource>() }
      it("emits a valid resource") {
        tracker.downloadGalaxyResource("48", "desrio").toFlowable()
          .subscribe(testSubscriber)
        testSubscriber.assertValue { it.name == "desrio" }
      }
      it("emits an error for an invalid resource") {
        tracker.downloadGalaxyResource("48", "a").toFlowable()
          .subscribe(testSubscriber)
        testSubscriber.assertError { true }
      }
    }
  }
})
