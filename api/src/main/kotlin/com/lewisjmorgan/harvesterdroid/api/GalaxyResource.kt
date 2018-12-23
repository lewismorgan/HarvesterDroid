package com.lewisjmorgan.harvesterdroid.api

import com.lewisjmorgan.harvesterdroid.api.resource.Attributes
import com.lewisjmorgan.harvesterdroid.api.resource.ResourceType
import java.util.*

class GalaxyResource(var partial: Boolean) {
  constructor(): this(false)

  var name: String = ""
  var container: String = ""

  var spawnDate: Date = Date()
  var despawnDate: Date = Date()

  val planets = ArrayList<String>()
  val attributes = HashMap<String, Int>()

  var resourceType: ResourceType = ResourceType()

  @Transient
  var resourceTypeString: String = ""

  val isActive = despawnDate != Date()

  fun getAttribute(name: String): Int {
    return attributes.getOrDefault(name, -1)
  }

  fun setAttribute(attribute: String, value: Int) {
    attributes[attribute] = value
  }

  override fun toString(): String {
    return ("GalaxyResource{" + "name='" + name + '\''.toString() + ", resourceType='" + resourceType + '\''.toString()
        + ", container='" + container + '\''.toString() + '}'.toString())
  }
}