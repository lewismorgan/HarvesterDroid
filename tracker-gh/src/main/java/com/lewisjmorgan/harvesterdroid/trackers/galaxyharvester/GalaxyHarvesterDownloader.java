/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lewisjmorgan.harvesterdroid.trackers.galaxyharvester;

import com.lewisjmorgan.harvesterdroid.Downloader;
import com.lewisjmorgan.harvesterdroid.GalaxyResource;
import com.lewisjmorgan.harvesterdroid.resource.ResourceType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

@Deprecated
class OldGalaxyHarvesterDownloader {

  private DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
  private HarvesterCurrentResourcesXml currentResourcesXml;

//  public GalaxyHarvesterDownloader() {
//    super("https://galaxyharvester.net/", new File("galaxyharvester"));
//  }

  @NotNull
  protected Map<String, GalaxyResource> parseCurrentResourcesList(@NotNull InputStream currentResourcesStream) throws IOException {
    try {
      if (xmlFactory == null) {
        xmlFactory = DocumentBuilderFactory.newInstance();
      }
      currentResourcesXml = new HarvesterCurrentResourcesXml(xmlFactory.newDocumentBuilder());
      currentResourcesXml.load(currentResourcesStream);
      return currentResourcesXml.getGalaxyResources();
    } catch (ParserConfigurationException | SAXException e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  protected Map<String, String> parseGalaxyList(InputStream galaxyListStream) {
    if (xmlFactory == null) {
      xmlFactory = DocumentBuilderFactory.newInstance();
    }

    try {
      HarvesterGalaxyListXml galaxyListXml = new HarvesterGalaxyListXml(xmlFactory.newDocumentBuilder());
      galaxyListXml.load(galaxyListStream);

      return galaxyListXml.getGalaxyList();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected GalaxyResource parseGalaxyResource(InputStream galaxyResourceStream) {
    try {
      HarvesterResourceXml resourceXml = new HarvesterResourceXml(xmlFactory.newDocumentBuilder());
      resourceXml.load(galaxyResourceStream);
      return resourceXml.getGalaxyResource();
    } catch (ParserConfigurationException | SAXException | IOException e1) {
      e1.printStackTrace();
    }
    return null;
  }

//  public InputStream createCurrentResourcesStream(@NotNull String galaxy) throws IOException {
//    return createInputStreamFromUrl("exports/current" + galaxy + ".xml");
//  }
//
//  protected InputStream createGalaxyResourceStream(@NotNull String galaxy, @NotNull String resource) throws IOException {
//    return createInputStreamFromUrl("getResourceByName.py?name=" + resource + "&galaxy=" + galaxy);
//  }
//
//
//  @Override
//  public Date getCurrentResourcesTimestamp() {
//    if (currentResourcesXml == null || currentResourcesXml.getTimestamp() == null
//        || currentResourcesXml.getTimestamp().isEmpty()) {
//      return null;
//    }
//    DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z");
//    try {
//      return dateFormat.parse(currentResourcesXml.getTimestamp());
//    } catch (ParseException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }

//  protected void downloadResourceTrees(@NotNull HashMap<String, ResourceType> typeMap, @NotNull HashMap<String, List<String>> groupMap)
//      throws IOException {
//    try {
//      if (xmlFactory == null) {
//        xmlFactory = DocumentBuilderFactory.newInstance();
//      }
//      HarvesterResourceTypeXml resourceTypeXml = new HarvesterResourceTypeXml(xmlFactory.newDocumentBuilder());
//      HarvesterResourceGroupXml resourceGroupXml = new HarvesterResourceGroupXml(xmlFactory.newDocumentBuilder());
//      HarvesterResourceTypeGroupXml resourceTypeGroupXml = new HarvesterResourceTypeGroupXml(xmlFactory.newDocumentBuilder());
//      resourceTypeXml.load(getListTypeStream("resource_type"));
//      resourceGroupXml.load(getListTypeStream("resource_group"));
//      resourceTypeGroupXml.load(getListTypeStream("resource_type_group"));
//
//      typeMap.putAll(resourceGroupXml.getResourceTypeMap());
//      typeMap.putAll(resourceTypeXml.getResourceTypeMap());
//      groupMap.putAll(resourceTypeGroupXml.getTypeGroupMap());
//
//    } catch (ParserConfigurationException | SAXException e) {
//      e.printStackTrace();
//    }
//  }
}
