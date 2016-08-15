package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;


public class CurrentResourcesXml extends BaseXml {
	private String timestamp;
	private Map<String, GalaxyResource> galaxyResources = new HashMap<>();

	public CurrentResourcesXml() {
		super(null);
	}

	public CurrentResourcesXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final void write(Document document) {
		throw new UnsupportedOperationException();
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Collection<GalaxyResource> getGalaxyResourceList() {
		return galaxyResources.values();
	}

	protected void setGalaxyResources(Map<String, GalaxyResource> galaxyResources) {
		this.galaxyResources = galaxyResources;
	}

	public Map<String, GalaxyResource> getGalaxyResources() {
		return galaxyResources;
	}
}
