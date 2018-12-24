package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyResourceRepository
import io.reactivex.Flowable
import java.io.InputStream
import java.io.OutputStream

class GalaxyResourceService(private val resourceRepository: GalaxyResourceRepository, private val tracker: Tracker) {
  private var updated = false
  var galaxy = Galaxy("", "")

  fun getResources(): Flowable<GalaxyResource> {
    return resourceRepository.getAll()
  }

  fun getCurrentResources(): Flowable<GalaxyResource> {
    // TODO Filter based on date
    return downloadLatestResources()
  }

  fun save(outputStream: OutputStream) {

  }

  fun load(inputStream: InputStream) {

  }

  private fun downloadLatestResources(): Flowable<GalaxyResource> {
    return tracker.downloadGalaxyResources(galaxy.id).doOnNext { item ->
      if (resourceRepository.exists(item)) {
        resourceRepository.remove(item.name)
        resourceRepository.add(item)
      }
    }.doOnComplete { updated = true }
  }

  fun hasUpdatedResources() = updated
}