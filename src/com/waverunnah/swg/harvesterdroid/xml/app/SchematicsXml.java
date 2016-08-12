package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for reading and saving schematic xml's unique to this program
 */
public class SchematicsXml extends BaseXml {

	private List<Schematic> schematicsList = new ArrayList<>();
	private List<String> professionList = new ArrayList<>();

	public SchematicsXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		schematicsList.clear();

		// <schematics>
		processElement(root, child -> {
			Schematic schematic = parseSchematic(child); // <schematic>...</schematic>
			if (schematic != null)
				schematicsList.add(schematic);
		});
		// </schematics>
	}

	@Override
	protected void write(OutputStream outputStream) {
		// TODO Write method for writing schematics xml
	}

	private Schematic parseSchematic(Node node) {
		if (node.getAttributes().getLength() != 2)
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

			schematic.getModifiers().put(((Element) child).getAttribute("id"),
					Float.valueOf(((Element) child).getAttribute("value")) / 100.0f);
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

			schematic.getResources().put(((Element) child).getAttribute("id"),
					Integer.valueOf(((Element) child).getAttribute("count")));
		}
	}

	public List<Schematic> getSchematicsList() {
		return schematicsList;
	}

	public List<String> getProfessionList() {
		return professionList;
	}
}
