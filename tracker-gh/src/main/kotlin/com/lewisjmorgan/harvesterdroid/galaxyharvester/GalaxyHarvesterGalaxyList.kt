package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyList

@Suppress("MemberVisibilityCanBePrivate", "PropertyName")
class GalaxyHarvesterGalaxyList: GalaxyList {
  val galaxy_values: MutableList<String> = mutableListOf()
  val galaxy_names: MutableList<String> = mutableListOf()
  val galaxy_prop1: MutableList<String> = mutableListOf()

  override fun getGalaxies(): List<Galaxy> {
    // Construct the galaxies
    val galaxyList = mutableListOf<Galaxy>()
    if (galaxy_values.size == galaxy_names.size && galaxy_names.size == galaxy_prop1.size) {
      galaxy_values.forEachIndexed { index, id ->
        // Only want the active galaxies
        if (galaxy_prop1[index] == "Active") {
          galaxyList.add(Galaxy(id, galaxy_names[index]))
        }
      }
    }
    return galaxyList.toList()
  }
}