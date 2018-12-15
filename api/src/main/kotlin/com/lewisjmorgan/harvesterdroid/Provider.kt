package com.lewisjmorgan.harvesterdroid

import com.lewisjmorgan.harvesterdroid.resource.ResourceType
import java.util.HashMap

abstract class AbstractDataProvider<T: Downloader>(val name: String, protected val downloader: T): IGalaxyProvider, IGalaxyResourceProvider {
  // These are populated through the provider
  val resourceGroups = HashMap<String, List<String>>()
  val resourceTypes = HashMap<String, ResourceType>()

  private val dataStore = ProviderDataStore()

//  val galacticResources = HashMap<String, List<GalaxyResource>>()
//  val galaxies = HashMap<String, String>() // Key: Galaxy ID, Value: Name

  override fun provideGalaxies(): List<Galaxy> {
    // TODO: Return an Rx list for proper data handling
    val galaxies = dataStore.provideGalaxies()
    return if (galaxies.isEmpty()) {
      // Try to get the cached galaxies from the data store
      val storedGalaxies = dataStore.provideGalaxies()
      if (storedGalaxies.isEmpty()) {
        val galaxyList = downloader.downloadGalaxyList()
        dataStore.replaceGalaxiesWith(galaxyList)
        galaxyList
      } else {
        storedGalaxies
      }
    } else {
      galaxies
    }
  }

  override fun provideCurrentGalaxyResources(galaxy: Galaxy): List<GalaxyResource>  {
    // TODO: Return an Rx list for proper data handling
    val storedResources = dataStore.provideCurrentGalaxyResources(galaxy)
    return if (storedResources.isEmpty()) {
      val currentResources = downloader.downloadActiveGalaxyResources(galaxy)
      dataStore.insertResources(galaxy, currentResources)
      currentResources
    } else {
      storedResources
    }
  }

  override fun provideGalaxyResource(galaxy: Galaxy, name: String): GalaxyResource? {
    val stored = dataStore.provideGalaxyResource(galaxy, name)
    return if (stored == null) {
      val downloaded = downloader.downloadGalaxyResource(galaxy, name)
      dataStore.insertResources(galaxy, listOf(downloaded))
      downloaded
    } else {
      stored
    }
  }

  fun getResourceGroups(group: String): List<String> {
    return resourceGroups.getOrDefault(group, listOf())
  }

//  private fun populateGalaxyResourceInfo(galaxyResource: GalaxyResource) {
//    val type = resourceTypes.getOrDefault(galaxyResource.resourceTypeString, ResourceType())
//    galaxyResource.resourceType = type
//  }
}
