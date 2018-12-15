package com.lewisjmorgan.harvesterdroid.repository

import com.lewisjmorgan.harvesterdroid.GalaxyResource
import io.reactivex.Flowable
import io.reactivex.Single

interface GalaxyResourceRepository {
  fun get(resource: String): Single<GalaxyResource>
  fun add(resource: GalaxyResource)
  fun remove(resource: String): Single<Boolean>
  fun getAll(): Flowable<GalaxyResource>
}
