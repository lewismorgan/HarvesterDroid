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

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for reading and saving Harvester Droid Schematic XML's
 */
public class SchematicsXml extends BaseXml {
	private List<Schematic> schematics = new ArrayList<>();

	public SchematicsXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		schematics.clear();

		if (!root.getNodeName().equals("schematics"))
			return;

		// <schematics>
		processElement(root, child -> {
			Schematic schematic = parseSchematic(child); // <schematic>...</schematic>
			if (schematic != null)
				schematics.add(schematic);
		});
		// </schematics>
	}

	@Override
	protected void write(Document document) {
		Element root = document.createElement("schematics");
		schematics.forEach(schematic -> createSchematicElement(document, root, schematic));
		document.appendChild(root);
	}

	private void createSchematicElement(Document document, Element root, Schematic schematic) {
		Element self = document.createElement("schematic");
		self.setAttribute("name", schematic.getName());
		self.setAttribute("group", schematic.getGroup());

		Element resources = document.createElement("resources");
		schematic.getResources().forEach(resource -> createResourceElement(document, resources, resource));

		Element modifiers = document.createElement("modifiers");
		schematic.getModifiers().forEach(modifier -> createModifierElement(document, modifiers, modifier));

		root.appendChild(self); // <schematic>
		self.appendChild(resources); // <resources>...</resources>
		self.appendChild(modifiers); // <modifiers>...</modifiers>
	}

	private void createModifierElement(Document document, Element modifiers, Schematic.Modifier modifier) {
		Element self = document.createElement("modifier");
		self.setAttribute("id", modifier.getName());
		self.setAttribute("value", Integer.toString(modifier.getValue()));
		modifiers.appendChild(self);
	}

	private void createResourceElement(Document document, Element resources, String resource) {
		Element self = document.createElement("resource");
		self.setAttribute("id", resource);
		resources.appendChild(self);
	}

	private Schematic parseSchematic(Node node) {
		if (!node.getNodeName().equals("schematic") || node.getAttributes().getLength() != 2)
			return null;

		Schematic schematic = new Schematic();
		schematic.setName(node.getAttributes().getNamedItem("name").getTextContent());
		String profession = node.getAttributes().getNamedItem("group").getTextContent();
		schematic.setGroup(profession);

		processElement(node, child -> {
			switch(child.getNodeName()) {
				case "resources":
					parseResources(child, schematic);
					break;
				case "modifiers":
					parseModifiers(child, schematic);
					break;
				default:
					break;
			}
		});

		return schematic;
	}

	private void parseModifiers(Node node, Schematic schematic) {
		processElement(node, child -> {
			if (!child.getNodeName().equals("modifier"))
				return;

			schematic.getModifiers().add(new Schematic.Modifier(((Element) child).getAttribute("id"),
					Integer.parseInt(((Element) child).getAttribute("value"))));
		});
	}

	private void parseResources(Node node, Schematic schematic) {
		NodeList children = node.getChildNodes();
		if (children.getLength() <= 0)
			return;

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (!(child instanceof Element))
				continue;

			if (!child.getNodeName().equals("resource"))
				continue;

			schematic.getResources().add(((Element) child).getAttribute("id"));
		}
	}

	public List<Schematic> getSchematics() {
		return schematics;
	}

	public void setSchematics(List<Schematic> schematics) {
		this.schematics = schematics;
	}
}
