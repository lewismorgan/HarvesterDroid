package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyListRepository
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
