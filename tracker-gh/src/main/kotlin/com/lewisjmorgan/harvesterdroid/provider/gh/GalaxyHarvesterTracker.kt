package com.lewisjmorgan.harvesterdroid.provider.gh

import com.lewisjmorgan.harvesterdroid.Galaxy
import com.lewisjmorgan.harvesterdroid.GalaxyResource
import com.lewisjmorgan.harvesterdroid.Tracker
import com.lewisjmorgan.harvesterdroid.trackers.galaxyharvester.HarvesterGalaxyListXml
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import com.lewisjmorgan.harvesterdroid.trackers.galaxyharvester.HarvesterResourceXml
import java.net.URI
import java.net.URL


class GalaxyHarvesterTracker: Tracker {
  private val baseUri: URI = URL("https://galaxyharvester.net/").toURI()
  override val id: String = "GalaxyHarvester"
  private val getListType = "getList.py?listType"

  val xmlFactory by lazy { DocumentBuilderFactory.newInstance() }

  override fun downloadGalaxies(): Flowable<Galaxy> {
    return Observable.create<Galaxy> { emitter ->
      val inputStream = createInputStreamFromBaseUrl("$getListType=galaxy")
      try {
        val galaxyListXml = HarvesterGalaxyListXml(xmlFactory.newDocumentBuilder())
        galaxyListXml.load(inputStream)
        // TODO Emit elements as they're parsed will be much better, will require XML refactor.
        galaxyListXml.galaxyList.forEach { (k, v) -> emitter.onNext(Galaxy(k, v)) }
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
        val resourceXml = HarvesterResourceXml(xmlFactory.newDocumentBuilder())
        resourceXml.load(inputStream)
        if (resourceXml.galaxyResource != null)
          emitter.onSuccess(resourceXml.galaxyResource)
        else emitter.onError(Throwable("Failed parsing resource $resource"))
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

//
//  override fun createGalaxyListResourceStream(): InputStream {
//    return createInputStreamFromBaseUrl("$getListType=galaxy")
//  }

}