package com.lewisjmorgan.harvesterdroid.service

import com.lewisjmorgan.harvesterdroid.Galaxy
import com.lewisjmorgan.harvesterdroid.Tracker
import com.lewisjmorgan.harvesterdroid.repository.GalaxyListRepository
import io.reactivex.Flowable

class GalaxyListService(private val repository: GalaxyListRepository, private val tracker: Tracker) {
  private var recentlyUpdated = true
  fun getGalaxies(): Flowable<Galaxy> {
    return if (recentlyUpdated) {
      repository.getAll()
    } else {
      tracker.downloadGalaxies()
        .doOnNext { resource -> repository.add(resource) }
        .doOnComplete { recentlyUpdated = true }
    }
  }
}
