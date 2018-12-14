package com.lewisjmorgan.harvesterdroid.api

import com.lewisjmorgan.harvesterdroid.api.resource.ResourceType
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
import java.util.function.Consumer

/**
 * Represents a class for downloading data from a tracker
 *
 * <p>This base class should have no knowledge of "how" the data is stored, only that it can be downloaded
 * from some location and turned into something usable for HarvesterDroid. The responsibility lies on
 * sub-classes to know how the data is stored and convert them to the resources map.
 */

abstract class Downloader(private val root: String, val identifier: String, baseUrl: String, public var galaxyName: String) {
  private val logger = LoggerFactory.getLogger(Downloader::class.java)

  private val baseUri: URI = URL(baseUrl).toURI()

  // TODO : Use custom resource grouping for the application instead of relying on GalaxyHarvester
  // this'll make it easier in abstracting tracker functionality
  protected val currentResources = HashMap<String, GalaxyResource>()
  public val resourceTypeMap = HashMap<String, ResourceType>()
  protected val resourceGroups = HashMap<String, List<String>>()

  @Throws(IOException::class)
  protected abstract fun parseCurrentResourcesList(currentResourcesStream: InputStream)

  protected abstract fun parseGalaxyList(galaxyListStream: InputStream): Map<String, String>

  protected abstract fun parseGalaxyResource(galaxyResourceStream: InputStream): GalaxyResource?

  @Throws(IOException::class)
  protected abstract fun getCurrentResourcesStream(): InputStream

  @Throws(IOException::class)
  protected abstract fun getGalaxyResourceStream(resource: String): InputStream

  @Throws(IOException::class)
  protected abstract fun createGalaxyListStream(): InputStream

  abstract fun getCurrentResourcesTimestamp(): Date

  fun downloadGalaxyList(): Map<String, String>? {
    val file=File(getRootDownloadsPath() + "servers.dl")
    if (!file.exists() && !file.mkdirs()) {
      return null
    }

    try {
      createGalaxyListStream().use { inputStream ->
        Files.copy(inputStream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING)
        return parseGalaxyList(FileInputStream(file))
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }

    return null
  }

  @Throws(IOException::class)
  protected abstract fun downloadResourceTypes()

  @Throws(IOException::class)
  fun downloadCurrentResources(): DownloadResult {

    if (resourceTypeMap.isEmpty()) {
      // Resource types are not populated yet, go ahead and download them
      downloadResourceTypes()
    }

    val file = File(getRootDownloadsPath() + "/current_resources_" + galaxyName + ".dl")

    if (!file.exists() && !file.mkdirs()) {
      return DownloadResult.FAILED
    }

    try {
      getCurrentResourcesStream().use { `in` -> Files.copy(`in`, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING) }
    } catch (e: ConnectException) {
      return DownloadResult.FAILED
    }

    FileInputStream(file).use { fileInputStream -> parseCurrentResourcesList(fileInputStream) }

    currentResources.values.forEach(Consumer<GalaxyResource> { this.populateResourceFromType(it) })

    return DownloadResult.SUCCESS
  }

  private fun populateResourceFromType(galaxyResource: GalaxyResource) {
    val type = resourceTypeMap.getOrDefault(galaxyResource.resourceTypeString, ResourceType())
    galaxyResource.resourceType = type
  }

  fun downloadGalaxyResource(resource: String): GalaxyResource? {
    try {
      val galaxyResource=parseGalaxyResource(getGalaxyResourceStream(resource))
      if (galaxyResource != null) {
        populateResourceFromType(galaxyResource)
      }
      return galaxyResource
    } catch (e: IOException) {
      throw RuntimeException("Error downloading resource $resource")
    }

  }

  protected fun populateCurrentResourcesMap(parsedCurrentResources: Map<String, GalaxyResource>) {
    currentResources.clear()
    currentResources.putAll(parsedCurrentResources)
  }

  @Throws(IOException::class)
  fun getInputStreamFromUrl(url: String): InputStream {
    return baseUri.resolve(url).toURL().openStream()
  }

  fun getCurrentResources(): Collection<GalaxyResource> {
    return currentResources.values
  }

  private fun getRootDownloadsPath(): String {
    return "$root/$identifier"
  }

  fun getResourcesPath(): String {
    return "${getRootDownloadsPath()}/resources_$galaxyName.bson"
  }

  fun getResourceGroups(group: String): List<String> {
    return resourceGroups.getOrDefault(group, listOf())
  }

  enum class DownloadResult {
    FAILED,
    SUCCESS
  }

}