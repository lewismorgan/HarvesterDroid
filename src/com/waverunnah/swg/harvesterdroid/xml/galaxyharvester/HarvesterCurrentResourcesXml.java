package com.waverunnah.swg.harvesterdroid.xml.galaxyharvester;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.Planet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

public final class HarvesterCurrentResourcesXml extends CurrentResourcesXml {

	public HarvesterCurrentResourcesXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {

		// <resources as_of_date="timestamp">
		setTimestamp(root.getAttribute("as_of_date"));

		processElement(root, child -> {
			// <resource>...</resource>

			GalaxyResource resource = parseGalaxyResource(child);
			if (resource != null)
				getGalaxyResources().put(resource.getName(), resource);
		});
		// </resources>
	}

	private GalaxyResource parseGalaxyResource(Node node) {
		GalaxyResource galaxyResource = new GalaxyResource();

		processElement(node, child -> {
			switch(child.getNodeName()) {
				case "name":
					galaxyResource.setName(child.getTextContent());
					break;
				case "enter_date":
					galaxyResource.setDate(child.getTextContent());
					break;
				case "resource_type":
					galaxyResource.setResourceType(((Element) child).getAttribute("id"));
					break;
				case "group_id":
					galaxyResource.setContainer(child.getTextContent());
					break;
				case "stats":
					parseResourceStats(child, galaxyResource);
					break;
				case "planets":
					parsePlanets(child, galaxyResource);
				default:
					break;
			}
		});

		return galaxyResource;
	}

	private void parsePlanets(Node node, GalaxyResource galaxyResource) {
		processElement(node, child -> {
			if (!child.getNodeName().equals("planet"))
				return;

			galaxyResource.getPlanets().add(Planet.toPlanet(child.getTextContent()));
		});
	}

	private void parseResourceStats(Node node, GalaxyResource galaxyResource) {
		processElement(node, child -> {
			switch (child.getNodeName()) {
				case "ER":
					galaxyResource.setAttribute("entangle_resistance", Integer.valueOf(child.getTextContent()));
					break;
				case "CR":
					galaxyResource.setAttribute("cold_resistance", Integer.valueOf(child.getTextContent()));
					break;
				case "CD":
					galaxyResource.setAttribute("conductivity", Integer.valueOf(child.getTextContent()));
					break;
				case "DR":
					galaxyResource.setAttribute("decay_resistance", Integer.valueOf(child.getTextContent()));
					break;
				case "FL":
					galaxyResource.setAttribute("flavor", Integer.valueOf(child.getTextContent()));
					break;
				case "HR":
					galaxyResource.setAttribute("heat_resistance", Integer.valueOf(child.getTextContent()));
					break;
				case "MA":
					galaxyResource.setAttribute("malleability", Integer.valueOf(child.getTextContent()));
					break;
				case "PE":
					galaxyResource.setAttribute("potential_energy", Integer.valueOf(child.getTextContent()));
					break;
				case "OQ":
					galaxyResource.setAttribute("overall_quality", Integer.valueOf(child.getTextContent()));
					break;
				case "SR":
					galaxyResource.setAttribute("shock_resistance", Integer.valueOf(child.getTextContent()));
					break;
				case "UT":
					galaxyResource.setAttribute("unit_toughness", Integer.valueOf(child.getTextContent()));
					break;
				default:
					break;
			}
		});
	}
}
