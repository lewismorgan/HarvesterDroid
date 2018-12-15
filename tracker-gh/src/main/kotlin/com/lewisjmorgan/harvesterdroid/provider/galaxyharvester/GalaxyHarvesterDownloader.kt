package com.lewisjmorgan.harvesterdroid.provider.galaxyharvester

import com.lewisjmorgan.harvesterdroid.Downloader
import com.lewisjmorgan.harvesterdroid.Galaxy
import com.lewisjmorgan.harvesterdroid.GalaxyResource
import java.io.InputStream

class GalaxyHarvesterDownloader: Downloader("http://www.galaxyharvester.com/") {
  override fun parseActiveGalaxyResourcesStream(activeResourcesStream: InputStream): List<GalaxyResource> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun parseGalaxyList(galaxyListStream: InputStream): List<Galaxy> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun parseGalaxyResource(galaxyResourceStream: InputStream): GalaxyResource {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createActiveGalaxyResourcesStream(galaxy: Galaxy): InputStream {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createGalaxyListResourceStream(): InputStream {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createGalaxyResourceStream(galaxy: Galaxy, resource: String): InputStream {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}