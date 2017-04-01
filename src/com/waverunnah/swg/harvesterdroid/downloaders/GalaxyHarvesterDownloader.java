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

package com.waverunnah.swg.harvesterdroid.downloaders;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterCurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterResourceXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GalaxyHarvesterDownloader extends Downloader {

    private DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
    private HarvesterCurrentResourcesXml currentResourcesXml;

    public GalaxyHarvesterDownloader(String galaxy) {
        super("galaxyharvester", "https://galaxyharvester.net/", galaxy);
    }

    @Override
    protected void parseCurrentResources(InputStream currentResourcesStream) throws IOException {
        try {
            if (xmlFactory == null)
                xmlFactory = DocumentBuilderFactory.newInstance();
            currentResourcesXml = new HarvesterCurrentResourcesXml(xmlFactory.newDocumentBuilder());
            currentResourcesXml.load(currentResourcesStream);

            populateCurrentResourcesMap(currentResourcesXml.getGalaxyResources());
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public InputStream getCurrentResourcesStream() throws IOException {
        return getInputStreamFromUrl("exports/current" + getGalaxy() + ".xml");
    }

    @Override
    protected InputStream getGalaxyResourceStream(String resource) throws IOException {
        return getInputStreamFromUrl("getResourceByName.py?name=" + resource + "&galaxy=" + getGalaxy());
    }


    @Override
    public Date getCurrentResourcesTimestamp() {
        if (currentResourcesXml == null || currentResourcesXml.getTimestamp() == null
                || currentResourcesXml.getTimestamp().isEmpty())
            return null;
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z");
        try {
            return dateFormat.parse(currentResourcesXml.getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
