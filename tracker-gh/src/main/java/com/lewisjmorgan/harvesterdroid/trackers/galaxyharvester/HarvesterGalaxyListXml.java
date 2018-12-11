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

import com.lewisjmorgan.harvesterdroid.api.xml.BaseXml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Created by Waverunner on 4/1/17.
 */
public class HarvesterGalaxyListXml extends BaseXml {
  private Map<String, String> galaxyList = new HashMap<>();

  public HarvesterGalaxyListXml(DocumentBuilder documentBuilder) {
    super(documentBuilder);
  }

  @Override
  protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
    // <list_data>
    List<String> galaxyIds = new ArrayList<>();
    List<String> galaxyNames = new ArrayList<>();
    List<String> galaxyActive = new ArrayList<>();

    processElement(root, node -> {
      switch (node.getNodeName()) {
        case "galaxy_values":
          processElement(node, galaxyValue -> galaxyIds.add(galaxyValue.getTextContent()));
          break;
        case "galaxy_names":
          processElement(node, galaxyName -> galaxyNames.add(galaxyName.getTextContent()));

          break;
        case "galaxy_prop1":
          processElement(node, galaxyProp -> galaxyActive.add(galaxyProp.getTextContent()));
          break;
        default:
          break;
      }
    });

    if (galaxyIds.size() != galaxyNames.size() || galaxyIds.size() != galaxyActive.size()) {
      return;
    }

    for (int i = 0; i < galaxyIds.size(); i++) {
      if (!galaxyActive.get(i).equals("Active")) {
        continue;
      }
      galaxyList.put(galaxyIds.get(i), galaxyNames.get(i));
    }
    // </list_data>
  }

  @Override
  protected void write(Document document) {
    throw new UnsupportedOperationException();
  }

  public Map<String, String> getGalaxyList() {
    return galaxyList;
  }
}
