package com.waverunnah.swg.harvesterdroid.xml.galacticharvester;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.Planet;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class HarvesterResourcesXml extends BaseXml {
	private String timestamp;
	private List<GalaxyResource> galaxyResourceList = new ArrayList<>();
	private List<String> types = new ArrayList<>();

	public HarvesterResourcesXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		galaxyResourceList.clear();

		// <resources as_of_date="timestamp">
		timestamp = root.getAttribute("as_of_date");

		processElement(root, child -> {
			// <resource>...</resource>

			GalaxyResource resource = parseGalaxyResource(child);
			if (resource != null)
				galaxyResourceList.add(resource);
		});
		// </resources>
	}

	@Override
	protected void write(OutputStream outputStream) {
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
				case "group_id": // TODO Refactor
					galaxyResource.setContainer(child.getTextContent());
					if (!types.contains(child.getTextContent()))
						types.add(child.getTextContent());
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

	public String getTimestamp() {
		return timestamp;
	}

	public List<GalaxyResource> getGalaxyResourceList() {
		return galaxyResourceList;
	}

	public List<String> getTypes() {
		return types;
	}
}
