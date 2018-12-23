package com.lewisjmorgan.harvesterdroid.api

import io.reactivex.Flowable
import io.reactivex.Single

interface Tracker {
  val id: String
  fun createDataFactory(): DataFactory
  fun downloadGalaxies(): Flowable<Galaxy>
  fun downloadGalaxyResource(galaxyId: String, resource: String): Single<GalaxyResource>
  fun downloadGalaxyResources(galaxyId: String): Flowable<GalaxyResource>
}