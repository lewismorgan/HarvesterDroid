package com.lewisjmorgan.harvesterdroid

import com.lewisjmorgan.harvesterdroid.resource.Attributes
import com.lewisjmorgan.harvesterdroid.resource.ResourceType
import java.util.*

class GalaxyResource {
  var name: String = ""
  var container: String = ""

  var spawnDate: Date = Date()
  var despawnDate: Date = Date()

  val planets = ArrayList<String>()
  val attributes = HashMap<String, Int>(Attributes.size())

  var resourceType: ResourceType = ResourceType()

  @Transient
  var resourceTypeString: String = ""

  val isActive = despawnDate != Date()

  init {
    // Initialize all attribute values with -1
    // TODO Refactor so there is no init method
    Attributes.forEach { primary, _ -> attributes[primary] = -1}
  }

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