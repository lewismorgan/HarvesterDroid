package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import io.reactivex.Flowable

class CachedGalaxyListRepository : GalaxyListRepository, CachedRepository<Galaxy>() {
  override val cache: MutableList<Galaxy> = mutableListOf()

  override fun add(galaxy: Galaxy) {
    cache.add(galaxy)
  }

  override fun getAll(): Flowable<Galaxy> {
    return Flowable.fromIterable(cache)
  }

}