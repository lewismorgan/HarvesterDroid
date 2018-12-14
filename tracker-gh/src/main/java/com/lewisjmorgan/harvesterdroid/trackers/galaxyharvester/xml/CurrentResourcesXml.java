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

package com.lewisjmorgan.harvesterdroid.trackers.galaxyharvester.xml;

import com.lewisjmorgan.harvesterdroid.GalaxyResource;
import com.lewisjmorgan.harvesterdroid.xml.BaseXml;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public abstract class CurrentResourcesXml extends BaseXml {

  private String timestamp;
  private Map<String, GalaxyResource> galaxyResources = new HashMap<>();

  public CurrentResourcesXml(DocumentBuilder documentBuilder) {
    super(documentBuilder);
  }

  @Override
  protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected final void write(Document document) {
    throw new UnsupportedOperationException();
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, GalaxyResource> getGalaxyResources() {
    return galaxyResources;
  }
}
