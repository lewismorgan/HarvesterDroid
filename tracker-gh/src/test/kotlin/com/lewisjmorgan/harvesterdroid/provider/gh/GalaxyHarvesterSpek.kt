package com.lewisjmorgan.harvesterdroid.provider.gh

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertTrue

class GalaxyHarvesterSpek: Spek({
  describe("GalaxyHarvesterDownloader") {
    val downloader by memoized { GalaxyHarvesterDownloader() }
    describe("when downloading the galaxy list") {
      val galaxies = downloader.downloadGalaxyList()
      it("it is not empty") {
        assertTrue { galaxies.isNotEmpty() }
      }
      it("it contains an identifiable galaxy") {
        assertTrue { galaxies[0].identifier.isNotEmpty() }
      }
    }
  }
})
