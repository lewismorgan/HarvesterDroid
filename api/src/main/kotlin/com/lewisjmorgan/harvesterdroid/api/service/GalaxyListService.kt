package com.lewisjmorgan.harvesterdroid.api.service

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import com.lewisjmorgan.harvesterdroid.api.repository.GalaxyListRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import java.io.InputStream
import java.io.OutputStream

class GalaxyListService(private val repository: GalaxyListRepository, private val tracker: Tracker) {
  private var updated = false
  private val dataFactory by lazy { DataFactory() }

  fun getGalaxies(): Flowable<Galaxy> {
    return if (!updated)
      repository.getAll()
    else downloadGalaxies()
  }

  private fun downloadGalaxies(): Flowable<Galaxy> {
    return tracker.downloadGalaxies()
      .doOnNext { resource -> repository.add(resource) }
      .doOnComplete { updated = true }
  }

  fun saveGalaxies(outputStream: OutputStream): OutputStream {
    return dataFactory.serialize(outputStream, getGalaxies().toList().blockingGet(), MappingType.JSON)
  }

  fun loadGalaxies(inputStream: InputStream): Flowable<Galaxy> {
    return Observable.create<Galaxy> { emitter ->
      val galaxies = dataFactory.deserialize<List<Galaxy>>(inputStream, MappingType.JSON)
      galaxies.forEach {
        repository.add(it)
        emitter.onNext(it)
      }
      emitter.onComplete()
    }.toFlowable(BackpressureStrategy.BUFFER)
  }

  fun hasUpdatedGalaxies() = updated
}
