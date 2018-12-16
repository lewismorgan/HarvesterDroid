package com.lewisjmorgan.harvesterdroid.provider.gh

import com.lewisjmorgan.harvesterdroid.api.Galaxy
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
  }
})
