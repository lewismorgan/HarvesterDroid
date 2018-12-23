package com.lewisjmorgan.harvesterdroid.galaxyharvester

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource
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
import javax.xml.parsers.ParserConfigurationException


class GalaxyHarvesterTracker: Tracker {
  private val baseUri: URI = URL("https://galaxyharvester.net/").toURI()
  override val id: String = "GalaxyHarvester"
  private val getListType = "getList.py?listType"

  override fun downloadGalaxies(): Flowable<Galaxy> {
    return Observable.create<Galaxy> { emitter ->
      val inputStream = createInputStreamFromBaseUrl("$getListType=galaxy")
      try {
        // TODO Add deserialization from XML
        //val galaxyListXml = xmlFactory.parse<GalaxyHarvesterGalaxyList>(InputStreamReader(inputStream))
        //galaxyListXml.getGalaxies().forEach { emitter.onNext(it) }
      } catch (e: ParserConfigurationException) {
        emitter.onError(e)
      } catch (e: SAXException) {
        emitter.onError(e)
      } catch (e: IOException) {
        emitter.onError(e)
      }
      emitter.onComplete()
    }.toFlowable(BackpressureStrategy.BUFFER)
  }

  override fun downloadGalaxyResource(galaxyId: String, resource: String): Single<GalaxyResource> {
    return Single.create { emitter ->
      val inputStream = createInputStreamFromBaseUrl("getResourceByName.py?name=$resource&galaxy=$galaxyId")
      try {
        emitter.onSuccess(GalaxyResource())
        // TODO Add deserialization from XML
        //val resourceXml = xmlFactory.parse<HarvesterResourceXml>(InputStreamReader(inputStream))
        //if (resourceXml.galaxyResource != null)
          //emitter.onSuccess(resourceXml.galaxyResource)
        //else emitter.onError(Throwable("Failed parsing resource $resource"))
      } catch (e1: ParserConfigurationException) {
        emitter.onError(e1)
      } catch (e1: SAXException) {
        emitter.onError(e1)
      } catch (e1: IOException) {
        emitter.onError(e1)
      }
    }
  }

  override fun downloadGalaxyResources(): Flowable<GalaxyResource> {
    TODO("Not implemented :[")
  }

  @Throws(IOException::class)
  private fun createInputStreamFromBaseUrl(resolve: String): InputStream {
    return baseUri.resolve(resolve)
      .toURL()
      .openStream()
  }
}