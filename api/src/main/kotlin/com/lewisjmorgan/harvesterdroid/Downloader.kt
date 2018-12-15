package com.lewisjmorgan.harvesterdroid

import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL

/**
 * Represents a class for telling a provider where to download data from
 */
abstract class Downloader(baseUrl: String) {
  private val logger = LoggerFactory.getLogger(Downloader::class.java)

  private val baseUri: URI = URL(baseUrl).toURI()

  protected abstract fun parseActiveGalaxyResourcesStream(activeResourcesStream: InputStream): List<GalaxyResource>

  protected abstract fun  parseGalaxyList(galaxyListStream: InputStream): List<Galaxy>

  protected abstract fun parseGalaxyResource(galaxyResourceStream: InputStream): GalaxyResource

  protected abstract fun createActiveGalaxyResourcesStream(galaxy: Galaxy): InputStream

  protected abstract fun createGalaxyListResourceStream(): InputStream

  protected abstract fun createGalaxyResourceStream(galaxy: Galaxy, resource: String): InputStream

  fun downloadActiveGalaxyResources(galaxy: Galaxy): List<GalaxyResource> {
    return parseActiveGalaxyResourcesStream(createActiveGalaxyResourcesStream(galaxy))
  }

  fun downloadGalaxyList(): List<Galaxy> {
    return parseGalaxyList(createGalaxyListResourceStream())
  }

  fun downloadGalaxyResource(galaxy: Galaxy, resource: String): GalaxyResource {
    return parseGalaxyResource(createGalaxyResourceStream(galaxy, resource))
  }

  @Throws(IOException::class)
  protected fun createInputStreamFromBaseUrl(resolve: String): InputStream {
    return baseUri.resolve(resolve)
      .toURL()
      .openStream()
  }
}