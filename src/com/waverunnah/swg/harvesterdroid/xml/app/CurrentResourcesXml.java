package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;

import javax.xml.parsers.DocumentBuilder;
import java.util.*;


public abstract class CurrentResourcesXml extends BaseXml {
	private String timestamp;
	private Map<String, GalaxyResource> galaxyResources = new HashMap<>();
	private List<String> types = new ArrayList<>();

	public CurrentResourcesXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
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

	public void setGalaxyResources(Map<String, GalaxyResource> galaxyResources) {
		this.galaxyResources = galaxyResources;
	}

	public Map<String, GalaxyResource> getGalaxyResources() {
		return galaxyResources;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
}
