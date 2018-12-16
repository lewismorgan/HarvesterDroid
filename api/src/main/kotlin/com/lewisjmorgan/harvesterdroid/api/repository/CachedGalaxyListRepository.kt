package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import io.reactivex.Flowable

/**
 * Repository for the Galaxy List that is stored in a cache.
 * @property galaxies MutableList<Galaxy>
 */
class CachedGalaxyListRepository : GalaxyListRepository {
  val galaxies: MutableList<Galaxy> = arrayListOf()

  override fun add(galaxy: Galaxy) {
    galaxies.add(galaxy)
  }

  override fun getAll(): Flowable<Galaxy> {
    return Flowable.fromIterable(galaxies)
  }

}