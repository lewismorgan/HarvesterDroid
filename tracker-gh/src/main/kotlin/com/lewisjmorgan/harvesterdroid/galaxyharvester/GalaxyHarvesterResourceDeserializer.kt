package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import com.lewisjmorgan.harvesterdroid.api.resource.Attributes

class GalaxyHarvesterResourceDeserializer: StdDeserializer<GalaxyResource> {
  constructor(vc: Class<Any>?): super(vc)
  constructor(): this(null)
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): GalaxyResource {
    val item = GalaxyResource()
    val node = p!!.readValueAsTree<JsonNode>()

    node.fields().forEach {
      when(it.key) {
        "spawnName" -> item.name = it.value.asText()
        // TODO: Resource Type conversion (resourceType & containerType)
        "entered" -> item.spawnDate = it.value.asText().dateFromTimestamp()
        "unavailable" -> item.despawnDate = it.value.asText().dateFromTimestamp()
        //"planet" -> item.planets.add(it.value.textValue())
        "CR", "CD", "DR", "FL", "HR", "MA", "PE", "OQ", "SR", "UT", "ER" -> {
          val value = it.value.asInt(-1)
          if (value > -1)
            item.setAttribute(Attributes.getFullName(it.key), value)
        }
        // NOTE: The following are only parsed from the Current Resources xml
        "name" -> {
          item.name = it.value.asText()
          item.partial = true
        }
        "enter_date" -> item.spawnDate = it.value.asText().dateFromString()
        "stats" -> {
          println(it.value.fields())
        }
        else -> println(it.key)
      }
    }

    return item
  }
}
