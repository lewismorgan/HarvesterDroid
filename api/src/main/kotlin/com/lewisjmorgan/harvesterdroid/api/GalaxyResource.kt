package com.lewisjmorgan.harvesterdroid.api

import com.lewisjmorgan.harvesterdroid.api.resource.ResourceType
import java.util.*

class GalaxyResource(var partial: Boolean) {
  constructor(): this(false)

  var name: String = ""
  var container: String = ""

  var spawnDate: Date = Date(0L)
  var despawnDate: Date = Date(0L)

  val planets = ArrayList<String>()
  val attributes = HashMap<String, Int>()

  var resourceType: ResourceType = ResourceType()

  var resourceTypeString: String = ""

  fun getAttribute(name: String): Int {
    return attributes.getOrDefault(name, -1)
  }

  fun setAttribute(attribute: String, value: Int) {
    attributes[attribute] = value
  }

  fun isSpawned(): Boolean {
    return despawnDate != Date(0L)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GalaxyResource

    if (partial != other.partial) return false
    if (name != other.name) return false
    if (container != other.container) return false
    if (spawnDate != other.spawnDate) return false
    if (despawnDate != other.despawnDate) return false
    if (planets != other.planets) return false
    if (attributes != other.attributes) return false
//    if (resourceType != other.resourceType) return false
    if (resourceTypeString != other.resourceTypeString) return false

    return true
  }

  override fun hashCode(): Int {
    var result = partial.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + container.hashCode()
    result = 31 * result + spawnDate.hashCode()
    result = 31 * result + despawnDate.hashCode()
    result = 31 * result + planets.hashCode()
    result = 31 * result + attributes.hashCode()
//    result = 31 * result + resourceType.hashCode()
    result = 31 * result + resourceTypeString.hashCode()
    return result
  }

  override fun toString(): String {
    return "GalaxyResource(partial=$partial, name='$name', container='$container', spawnDate=$spawnDate, despawnDate=$despawnDate, planets=$planets, attributes=$attributes, resourceType=$resourceType, resourceTypeString='$resourceTypeString')"
  }
}