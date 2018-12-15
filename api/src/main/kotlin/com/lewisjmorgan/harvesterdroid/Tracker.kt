package com.lewisjmorgan.harvesterdroid

import io.reactivex.Flowable
import io.reactivex.Single

interface Tracker {
  val id: String
  fun downloadGalaxyResources(): Flowable<GalaxyResource>
  fun downloadGalaxies(): Flowable<Galaxy>
  fun downloadGalaxyResource(galaxyId: String, resource: String): Single<GalaxyResource>
}