package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.lewisjmorgan.harvesterdroid.api.DataFactory
import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
import com.lewisjmorgan.harvesterdroid.api.MappingType
import com.lewisjmorgan.harvesterdroid.api.Tracker
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.ParserConfigurationException

class GalaxyHarvesterTracker: Tracker {
  private val baseUri: URI = URL("https://galaxyharvester.net/").toURI()
  override val id: String = "GalaxyHarvester"
  private val getListType = "getList.py?listType"

  private val dataFactory by lazy { createDataFactory() }

  override fun downloadGalaxies(): Flowable<Galaxy> {
    return Observable.create<Galaxy> { emitter ->
      val inputStream = createInputStreamFromBaseUrl("$getListType=galaxy")
      try {
        val galaxyListXml = dataFactory.deserialize<GalaxyHarvesterGalaxyList>(inputStream, MappingType.XML)
        galaxyListXml.getGalaxies().forEach { emitter.onNext(it) }
        emitter.onComplete()
      } catch (e: Throwable) {
        emitter.tryOnError(e)
      }
    }.toFlowable(BackpressureStrategy.BUFFER)
  }

  override fun downloadGalaxyResource(galaxyId: String, resource: String): Single<GalaxyResource> {
    return Single.create { emitter ->
      val inputStream = createInputStreamFromBaseUrl("getResourceByName.py?name=$resource&galaxy=$galaxyId")
      try {
        val resourceXml = dataFactory.deserialize<GalaxyResource>(inputStream, MappingType.XML)
        emitter.onSuccess(resourceXml)
      } catch (e: Throwable) {
        emitter.tryOnError(e)
      }
    }
  }

  override fun downloadGalaxyResources(galaxyId: String): Flowable<GalaxyResource> {
    return Observable.create<GalaxyResource> { emitter ->
      val inputStream = createInputStreamFromBaseUrl("exports/current$galaxyId.xml")
      try {
        val currentResources = dataFactory.deserialize<List<GalaxyResource>>(inputStream, MappingType.XML)
        currentResources.forEach { emitter.onNext(it) }
        emitter.onComplete()
      } catch(e: Throwable) {
        emitter.tryOnError(e)
      }
    }.toFlowable(BackpressureStrategy.BUFFER)
  }

  override fun createDataFactory(): DataFactory {
    val dataFactory = DataFactory()
    dataFactory.modules.add(GalaxyHarvesterDataModule())
    return dataFactory
  }

  @Throws(IOException::class)
  private fun createInputStreamFromBaseUrl(resolve: String): InputStream {
    return baseUri.resolve(resolve)
      .toURL()
      .openStream()
  }
}
