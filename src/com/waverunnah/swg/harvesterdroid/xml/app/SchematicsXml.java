package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for reading and saving schematic xml's unique to this program
 */
public class SchematicsXml extends BaseXml {

	private List<Schematic> schematicsList = new ArrayList<>();
	private List<String> professionList = new ArrayList<>();
	private List<Schematic> schematics;

	public SchematicsXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		schematicsList.clear();

		if (!root.getNodeName().equals("schematics"))
			return;

		// <schematics>
		processElement(root, child -> {
			Schematic schematic = parseSchematic(child); // <schematic>...</schematic>
			if (schematic != null)
				schematicsList.add(schematic);
		});
		// </schematics>
	}

	@Override
	protected void write(Document document) {
		Element root = document.createElement("schematics");
		schematicsList.forEach(schematic -> createSchematicElement(document, root, schematic));
		document.appendChild(root);
	}

	private void createSchematicElement(Document document, Element root, Schematic schematic) {
		Element self = document.createElement("schematic");
		self.setAttribute("name", schematic.getName());
		self.setAttribute("prof", schematic.getGroup());

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
		self.setAttribute("value", Float.toString(modifier.getValue()));
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
		String profession = node.getAttributes().getNamedItem("prof").getTextContent();
		schematic.setGroup(profession);

		if (!professionList.contains(profession))
			professionList.add(profession);

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
					Float.parseFloat(((Element) child).getAttribute("value"))));
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

	public List<Schematic> getSchematicsList() {
		return schematicsList;
	}

	public List<String> getProfessionList() {
		return professionList;
	}

	public void setSchematics(List<Schematic> schematics) {
		this.schematics = schematics;
	}
}
