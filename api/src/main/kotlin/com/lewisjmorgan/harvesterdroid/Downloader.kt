package com.lewisjmorgan.harvesterdroid

import com.lewisjmorgan.harvesterdroid.resource.ResourceType
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.collections.HashMap

/**
 * Represents a class for downloading data from a tracker
 *
 * <p>This base class should have no knowledge of "how" the data is stored, only that it can be downloaded
 * from some location and turned into something usable for HarvesterDroid. The responsibility lies on
 * sub-classes to know how the data is stored and convert them to the resources map.
 */

abstract class Downloader(baseUrl: String, val destinationDir: File) {
  private val logger = LoggerFactory.getLogger(Downloader::class.java)

  private val baseUri: URI = URL(baseUrl).toURI()

  @Throws(IOException::class)
  protected abstract fun parseCurrentResourcesList(currentResourcesStream: InputStream): MutableMap<String, GalaxyResource>

  protected abstract fun parseGalaxyList(galaxyListStream: InputStream): Map<String, String>

  protected abstract fun parseGalaxyResource(galaxyResourceStream: InputStream): GalaxyResource?

  @Throws(IOException::class)
  protected abstract fun createCurrentResourcesStream(galaxy: String): InputStream

  @Throws(IOException::class)
  protected abstract fun createGalaxyResourceStream(galaxy: String, resource: String): InputStream

  @Throws(IOException::class)
  protected abstract fun createGalaxyListStream(): InputStream

  fun downloadGalaxyList(): Map<String, String> {
    val galaxyListFile = destinationDir.resolve("servers.dl")
    if (!galaxyListFile.exists() && !galaxyListFile.mkdirs()) {
      return mapOf()
    }

    try {
      createGalaxyListStream().use { inputStream ->
        Files.copy(inputStream, Paths.get(galaxyListFile.toURI()), StandardCopyOption.REPLACE_EXISTING)
        return parseGalaxyList(FileInputStream(galaxyListFile))
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }

    return mapOf()
  }

  @Throws(IOException::class)
  protected abstract fun downloadResourceTrees(typeMap: HashMap<String, ResourceType>, groupMap: HashMap<String, List<String>>)

  @Throws(IOException::class)
  fun downloadCurrentResources(galaxy: String): List<GalaxyResource> {
    val resources = ArrayList<GalaxyResource>()
    val file = destinationDir.resolve("current_resources_$galaxy.dl")

    if (!file.exists() && !file.mkdirs()) {
      return resources
    }

    try {
      createCurrentResourcesStream(galaxy).use { `in` -> Files.copy(`in`, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING) }
    } catch (e: ConnectException) {
      return resources
    }

    FileInputStream(file).use { fileInputStream ->
      val list = parseCurrentResourcesList(fileInputStream)

    }

    return resources
  }

  fun downloadGalaxyResource(galaxy: String, resource: String): GalaxyResource? {
    try {
      return parseGalaxyResource(createGalaxyResourceStream(galaxy, resource))
    } catch (e: IOException) {
      throw RuntimeException("An error occurred downloading resource $resource from $galaxy")
    }
  }

  @Throws(IOException::class)
  fun createInputStreamFromUrl(url: String): InputStream {
    return baseUri.resolve(url).toURL().openStream()
  }
}