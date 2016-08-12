package com.waverunnah.swg.harvesterdroid.data.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalaxyResource {
    private String name;
    private String date;
    private String resourceType;
    private String groupId;

    private List<Planet> planets = new ArrayList<>();

    private Map<String, Integer> attributes = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getAttribute(String attr) {
    	return attributes.get(attr);
    }

    public void setAttribute(String attr, int value) {
    	attributes.put(attr, value);
    }

	public Map<String, Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Integer> attributes) {
		this.attributes = attributes;
	}

	public boolean hasAttribute(String attr) {
    	return attributes.containsKey(attr);
    }

	public void setPlanets(List<Planet> planets) {
		this.planets = planets;
	}

	public List<Planet> getPlanets() {
        return planets;
    }

    @Override
    public String toString() {
        return "GalaxyResource{" +
                "name='" + name + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
