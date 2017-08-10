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

import io.github.waverunner.harvesterdroid.data.resources.ResourceType;
import io.github.waverunner.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Waverunner on 4/3/2017
 */
public class HarvesterResourceGroupXml extends BaseXml {
    private Map<String, ResourceType> resourceTypeMap = new HashMap<>();

    public HarvesterResourceGroupXml(DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
        // <list_data>
        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> caps = new ArrayList<>();

        processElement(root, node -> {
            switch (node.getNodeName()) {
                case "resource_group_values":
                    processElement(node, id -> ids.add(id.getTextContent()));
                    break;
                case "resource_group_names":
                    processElement(node, name -> names.add(name.getTextContent()));

                    break;
                case "resource_group_prop1":
                    processElement(node, cap -> caps.add(cap.getTextContent()));
                    break;
            }
        });

        if (ids.size() != names.size() || ids.size() != caps.size())
            return;

        for (int i = 0; i < ids.size(); i++) {
            ResourceType resourceType = new ResourceType();
            resourceType.setId(ids.get(i));
            resourceType.setName(names.get(i));

            resourceTypeMap.put(ids.get(i), resourceType);
        }

        // </list_data>
    }

    @Override
    protected void write(Document document) {
        throw new UnsupportedOperationException();
    }

    public Map<String, ResourceType> getResourceTypeMap() {
        return resourceTypeMap;
    }
}
