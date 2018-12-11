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

package io.github.waverunner.harvesterdroid.trackers.galaxyharvester;

import com.lewismorgan.harvesterdroid.api.GalaxyResource;
import io.github.waverunner.harvesterdroid.trackers.galaxyharvester.xml.CurrentResourcesXml;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class HarvesterCurrentResourcesXml extends CurrentResourcesXml {

  public HarvesterCurrentResourcesXml(DocumentBuilder documentBuilder) {
    super(documentBuilder);
  }

  @Override
  protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {

    // <resources as_of_date="timestamp">
    setTimestamp(root.getAttribute("as_of_date"));

    processElement(root, child -> {
      // <resource>...</resource>

      GalaxyResource resource = parseGalaxyResource(child);
      if (resource != null) {
        getGalaxyResources().put(resource.getName(), resource);
      }
    });
    // </resources>
  }

  private GalaxyResource parseGalaxyResource(Node node) {
    GalaxyResource galaxyResource = new GalaxyResource();

    processElement(node, child -> {
      switch (child.getNodeName()) {
        case "name":
          galaxyResource.setName(child.getTextContent());
          break;
        case "enter_date":
          galaxyResource.setSpawnDate(formatDate(child.getTextContent()));
          break;
        case "resource_type":
          galaxyResource.setResourceTypeString(((Element) child).getAttribute("id"));
          break;
        case "group_id":
          galaxyResource.setContainer(child.getTextContent());
          break;
        case "stats":
          parseResourceStats(child, galaxyResource);
          break;
        case "planets":
          parsePlanets(child, galaxyResource);
          break;
        default:
          break;
      }
    });

    return galaxyResource;
  }

  private Date formatDate(String timestamp) {
    DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z");
    try {
      return dateFormat.parse(timestamp);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private void parsePlanets(Node node, GalaxyResource galaxyResource) {
    processElement(node, child -> {
      if (!child.getNodeName().equals("planet")) {
        return;
      }

      galaxyResource.getPlanets().add(child.getTextContent());
    });
  }

  private void parseResourceStats(Node node, GalaxyResource galaxyResource) {
    processElement(node, child -> {
      switch (child.getNodeName()) {
        case "ER":
          galaxyResource.setAttribute("entangle_resistance", Integer.valueOf(child.getTextContent()));
          break;
        case "CR":
          galaxyResource.setAttribute("cold_resistance", Integer.valueOf(child.getTextContent()));
          break;
        case "CD":
          galaxyResource.setAttribute("conductivity", Integer.valueOf(child.getTextContent()));
          break;
        case "DR":
          galaxyResource.setAttribute("decay_resistance", Integer.valueOf(child.getTextContent()));
          break;
        case "FL":
          galaxyResource.setAttribute("flavor", Integer.valueOf(child.getTextContent()));
          break;
        case "HR":
          galaxyResource.setAttribute("heat_resistance", Integer.valueOf(child.getTextContent()));
          break;
        case "MA":
          galaxyResource.setAttribute("malleability", Integer.valueOf(child.getTextContent()));
          break;
        case "PE":
          galaxyResource.setAttribute("potential_energy", Integer.valueOf(child.getTextContent()));
          break;
        case "OQ":
          galaxyResource.setAttribute("overall_quality", Integer.valueOf(child.getTextContent()));
          break;
        case "SR":
          galaxyResource.setAttribute("shock_resistance", Integer.valueOf(child.getTextContent()));
          break;
        case "UT":
          galaxyResource.setAttribute("unit_toughness", Integer.valueOf(child.getTextContent()));
          break;
        default:
          break;
      }
    });
  }
}
