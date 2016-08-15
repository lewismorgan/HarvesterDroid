package com.waverunnah.swg.harvesterdroid.xml.galacticharvester;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.Planet;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import com.waverunnah.swg.harvesterdroid.xml.app.ResourceXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
					galaxyResource.setResourceType(node.getTextContent());
					break;
				case "containerType":
					galaxyResource.setContainer(node.getTextContent());
					break;
				case "entered":
					galaxyResource.setDate(node.getTextContent());
					break;
				case "planet":
					galaxyResource.getPlanets().add(Planet.toPlanet(node.getTextContent()));
					break;
				case "unavailable":
					galaxyResource.setDespawnDate(node.getTextContent());
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
}
