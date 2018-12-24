package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import io.reactivex.Flowable
import io.reactivex.Single

interface GalaxyResourceRepository {
  fun get(resource: String): Single<GalaxyResource>
  fun add(resource: GalaxyResource)
  fun exists(resource: GalaxyResource): Boolean
  fun remove(resource: String)
  fun getAll(): Flowable<GalaxyResource>
}
