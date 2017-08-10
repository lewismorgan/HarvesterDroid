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

package io.github.waverunner.harvesterdroid.xml.galaxyharvester;

import io.github.waverunner.harvesterdroid.app.Attributes;
import io.github.waverunner.harvesterdroid.data.resources.GalaxyResource;
import io.github.waverunner.harvesterdroid.ui.dialog.ExceptionDialog;
import io.github.waverunner.harvesterdroid.xml.app.ResourceXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class HarvesterResourceXml extends ResourceXml {

    public HarvesterResourceXml(DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Override
    protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
        if (!root.getNodeName().equals("result"))
            return;

        GalaxyResource galaxyResource = new GalaxyResource();
        AtomicBoolean exists = new AtomicBoolean(false);
        processElement(root, node -> {
            switch (node.getNodeName()) {
                case "resultText":
                    exists.set(node.getTextContent().equals("found"));
                    break;
                case "spawnName":
                    galaxyResource.setName(node.getTextContent());
                    break;
                case "resourceType":
                    galaxyResource.setResourceTypeString(node.getTextContent());
                    break;
                case "containerType":
                    galaxyResource.setContainer(node.getTextContent());
                    break;
                case "entered":
                    galaxyResource.setDate(formatDate(node.getTextContent()));
                    break;
                case "planet":
                    galaxyResource.getPlanets().add(node.getTextContent());
                    break;
                case "unavailable":
                    galaxyResource.setDespawnDate(formatDate(node.getTextContent()));
                    break;
                case "CR":
                case "CD":
                case "DR":
                case "FL":
                case "HR":
                case "MA":
                case "PE":
                case "OQ":
                case "SR":
                case "UT":
                case "ER":
                    parseAttributes(galaxyResource, node);
                    break;
                default:
                    break;
            }
        });

        if (exists.get())
            setGalaxyResource(galaxyResource);
    }

    private void parseAttributes(GalaxyResource galaxyResource, Node node) {
        String text = node.getTextContent();
        if (text.equals("None") || text.isEmpty())
            return;

        try {
            int value = Integer.parseInt(text);
            galaxyResource.setAttribute(Attributes.getFullName(node.getNodeName()), value);
        } catch (NumberFormatException e) {
            ExceptionDialog.display(e);
        }
    }

    private Date formatDate(String timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        try {
            return dateFormat.parse(timestamp);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
