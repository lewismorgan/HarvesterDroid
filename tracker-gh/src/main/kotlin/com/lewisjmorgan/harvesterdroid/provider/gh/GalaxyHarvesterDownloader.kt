package com.lewisjmorgan.harvesterdroid.provider.gh

import com.lewisjmorgan.harvesterdroid.Downloader
import com.lewisjmorgan.harvesterdroid.Galaxy
import com.lewisjmorgan.harvesterdroid.GalaxyResource
import com.lewisjmorgan.harvesterdroid.trackers.galaxyharvester.HarvesterGalaxyListXml
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class GalaxyHarvesterDownloader: Downloader("https://galaxyharvester.net/") {
  private val getListType = "getList.py?listType"
  val xmlFactory by lazy { DocumentBuilderFactory.newInstance() }

  override fun parseActiveGalaxyResourcesStream(activeResourcesStream: InputStream): List<GalaxyResource> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun parseGalaxyList(galaxyListStream: InputStream): List<Galaxy> {
    val galaxies = ArrayList<Galaxy>()

    try {
      val galaxyListXml = HarvesterGalaxyListXml(xmlFactory.newDocumentBuilder())
      galaxyListXml.load(galaxyListStream)

      galaxies.addAll(galaxyListXml.galaxyList.flatMap { (key, value) -> listOf(Galaxy(key, value)) })
    } catch (e: ParserConfigurationException) {
      e.printStackTrace()
    } catch (e: SAXException) {
      e.printStackTrace()
    } catch (e: IOException) {
      e.printStackTrace()
    }

    return galaxies
  }

  override fun parseGalaxyResource(galaxyResourceStream: InputStream): GalaxyResource {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createActiveGalaxyResourcesStream(galaxy: Galaxy): InputStream {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createGalaxyListResourceStream(): InputStream {
    return createInputStreamFromBaseUrl("$getListType=galaxy")
  }

  override fun createGalaxyResourceStream(galaxy: Galaxy, resource: String): InputStream {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}