package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyResourceRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream

class GalaxyResourceService(private val resourceRepository: GalaxyResourceRepository, private val tracker: Tracker) {
  private var updated = false
  var galaxy = Galaxy("", "")

  fun getResources(): Flowable<GalaxyResource> {
    return resourceRepository.getAll()
  }

  fun getActiveResources(): Flowable<GalaxyResource> {
    return resourceRepository.getAll().filter { it.isSpawned() }
  }

  fun save(outputStream: OutputStream, dataFactory: DataFactory, mappingType: MappingType): Single<OutputStream> {
    return resourceRepository.getAll().toList()
      .map { dataFactory.serialize(outputStream, it, mappingType) }
  }

  fun load(inputStream: InputStream, dataFactory: DataFactory, mappingType: MappingType): Flowable<GalaxyResource> {
    return Observable.create<GalaxyResource> { emitter ->
      val resources = dataFactory.deserialize<List<GalaxyResource>>(inputStream, mappingType)
      resources.forEach { emitter.onNext(it) }
      emitter.onComplete()
    }.doOnNext { resourceRepository.add(it) }.toFlowable(BackpressureStrategy.BUFFER)
  }

  fun downloadLatestResources(): Flowable<GalaxyResource> {
    return tracker.downloadGalaxyResources(galaxy.id).doOnNext { item ->
      if (resourceRepository.exists(item)) {
        resourceRepository.remove(item.name)
      }
      resourceRepository.add(item)
    }.doOnComplete { updated = true }
  }

  fun hasUpdatedResources() = updated
}