package com.lewisjmorgan.harvesterdroid

import com.lewisjmorgan.harvesterdroid.resource.Attributes
import com.lewisjmorgan.harvesterdroid.resource.ResourceType
import java.util.*

class GalaxyResource {
  public var name: String = ""
  public var container: String = ""

  public var spawnDate: Date = Date()
  public var despawnDate: Date = Date()

  public val planets = ArrayList<String>()
  public val attributes = HashMap<String, Int>(Attributes.size())

  public var resourceType: ResourceType = ResourceType()

  @Transient
  public var resourceTypeString: String = ""

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