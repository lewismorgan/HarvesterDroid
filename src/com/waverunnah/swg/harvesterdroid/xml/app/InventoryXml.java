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

package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryXml extends BaseXml {
    public List<String> inventory = new ArrayList<>();

    public InventoryXml(DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Override
    protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
        if (!root.getNodeName().equals("inventory"))
            return;

        processElement(root, node -> {
            if (!node.getNodeName().equals("resource") || node.getAttributes().getLength() != 1)
                return;

            Node name = node.getAttributes().getNamedItem("name");
            if (name == null || name.getTextContent().isEmpty())
                return;

            inventory.add(name.getTextContent());
        });
    }

    @Override
    protected void write(Document document) {
        Element root = document.createElement("inventory");

        inventory.forEach(resource -> {
            Element node = document.createElement("resource");
            node.setAttribute("name", resource);
            root.appendChild(node);
        });

        document.appendChild(root);
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void setInventory(Collection<String> newInventory) {
        inventory.clear();
        inventory.addAll(newInventory);
    }
}
