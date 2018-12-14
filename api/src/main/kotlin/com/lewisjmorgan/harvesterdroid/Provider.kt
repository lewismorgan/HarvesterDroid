package com.lewisjmorgan.harvesterdroid

import com.lewisjmorgan.harvesterdroid.resource.ResourceType
import java.util.HashMap

abstract class Provider<T: Downloader>(protected val downloader: T) {
  // These are populated through the provider
  val resourceGroups = HashMap<String, List<String>>()

  // These are populated via the downloader
  val resourceTypes = HashMap<String, ResourceType>()
  val galacticResources = HashMap<String, List<GalaxyResource>>()
  val galaxies = HashMap<String, String>() // Key: Galaxy ID, Value: Name

  fun updateGalaxies() {
    val galaxyList = downloader.downloadGalaxyList()
    galaxies.clear()
    galaxies.putAll(galaxyList)
  }

  /**
   * Updates the resources
   * @param galaxy String
   */
  fun downloadLatestResources(galaxy: String) {
    val resources = downloader.downloadCurrentResources(galaxy)
    resources.forEach {
      populateGalaxyResourceType(it)
    }

    if(galacticResources.containsKey(galaxy)) {
      // Remove all resources from the loaded galaxy resources for the provider that exists in current resources as there could be new data
      val galaxyResources = galacticResources[galaxy]!!
      val names = resources.map(GalaxyResource::name)
      val removedDuplicates = galaxyResources.dropWhile { names.contains(it.name) }
      galacticResources[galaxy] = removedDuplicates.plus(resources)
    } else {
      galacticResources[galaxy] = resources
    }
  }

  fun getResourceGroups(group: String): List<String> {
    return resourceGroups.getOrDefault(group, listOf())
  }


  private fun populateGalaxyResourceType(galaxyResource: GalaxyResource) {
    val type = resourceTypes.getOrDefault(galaxyResource.resourceTypeString, ResourceType())
    galaxyResource.resourceType = type
  }
}
