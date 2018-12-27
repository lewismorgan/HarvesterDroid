package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyListRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream

class GalaxyListService(private val repository: GalaxyListRepository, private val tracker: Tracker) {
  private var updated = false

  fun getGalaxies(): Flowable<Galaxy> {
    return repository.getAll()
  }

  fun downloadGalaxies(): Flowable<Galaxy> {
    return tracker.downloadGalaxies()
      .doOnNext { resource -> repository.add(resource) }
      .doOnComplete { updated = true }
  }

  fun save(outputStream: OutputStream, dataFactory: DataFactory, mappingType: MappingType): Single<OutputStream> {
    return getGalaxies().toList().map { dataFactory.serialize(outputStream, it, mappingType) }
  }

  fun load(inputStream: InputStream, dataFactory: DataFactory, mappingType: MappingType): Flowable<Galaxy> {
    return Observable.create<Galaxy> { emitter ->
      val galaxies = dataFactory.deserialize<List<Galaxy>>(inputStream, mappingType)
      galaxies.forEach { emitter.onNext(it) }
      emitter.onComplete()
    }.doOnNext { repository.add(it) }.toFlowable(BackpressureStrategy.BUFFER)
  }

  fun hasUpdatedGalaxies() = updated
}
