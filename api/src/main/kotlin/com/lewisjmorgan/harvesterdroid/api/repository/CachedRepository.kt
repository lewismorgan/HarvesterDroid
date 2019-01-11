package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.MappingType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.io.InputStream
import java.io.OutputStream

// TODO Create test for save and load functions using a mock implementation

abstract class CachedRepository<T: Any> {
  protected abstract val cache: MutableList<T>

  /**
   * Saves the currently cached items, serializing with a DataFactory into the provided OutputStream
   * @param outputStream OutputStream
   * @param dataFactory DataFactory
   * @param mappingType MappingType
   * @return Single<OutputStream> the serialized cached data from the provided OutputStream
   */
  fun save(outputStream: OutputStream, dataFactory: DataFactory, mappingType: MappingType): Single<OutputStream> {
    return cache.toObservable().map { dataFactory.serialize(outputStream, it, mappingType) }.singleOrError()
  }

  /**
   * Loads a list of data into the repository cache using the given input stream of data. InputStream is deserialized using the provided DataFactory
   * and mapping type. Data must be saved from the same MappingType in order to be loaded properly.
   * @param inputStream InputStream that contains the data to deserialize
   * @param dataFactory DataFactory to use to deserialize the list of data
   * @param mappingType MappingType that was used to serialize the data
   * @return Flowable<T> of added items to the cache
   */
  fun load(inputStream: InputStream, dataFactory: DataFactory, mappingType: MappingType): Flowable<T> {
    return Observable.create<T> { emitter ->
      val deserializedData = dataFactory.deserialize<List<T>>(inputStream, mappingType)
      deserializedData.forEach { emitter.onNext(it) }
      emitter.onComplete()
    }.doOnNext { cache.add(it) }.toFlowable(BackpressureStrategy.BUFFER)
  }
}
