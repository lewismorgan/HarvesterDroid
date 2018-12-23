package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.fasterxml.jackson.databind.module.SimpleModule
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource

class GalaxyHarvesterDataModule : SimpleModule("GalaxyHarvester") {
  init {
    addDeserializer(GalaxyResource::class.java, GalaxyHarvesterResourceDeserializer())
  }
}