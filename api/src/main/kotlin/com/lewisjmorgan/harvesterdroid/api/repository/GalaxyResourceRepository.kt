package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import io.reactivex.Flowable
import io.reactivex.Single

interface GalaxyResourceRepository {
  fun get(resource: String): Single<GalaxyResource>
  fun add(resource: GalaxyResource)
  fun remove(resource: String): Single<Boolean>
  fun getAll(): Flowable<GalaxyResource>
}
