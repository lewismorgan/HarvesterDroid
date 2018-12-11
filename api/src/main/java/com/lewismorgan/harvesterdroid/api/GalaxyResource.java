package com.lewismorgan.harvesterdroid.api;

import com.lewismorgan.harvesterdroid.api.resource.ResourceType;
import com.lewismorgan.harvesterdroid.api.resource.Attributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Waverunner on 8/11/17.
 */
public class GalaxyResource {
  private String name;
  private String container;

  private Date spawnDate;
  private Date despawnDate;
  private List<String> planets;
  private Map<String, Integer> attributes;

  private ResourceType resourceType;

  private transient String resourceTypeString;

  public GalaxyResource() {
    planets = new ArrayList<>();
    attributes = new HashMap<>(Attributes.size());
    Attributes.forEach((primary, secondary) -> attributes.put(primary, -1));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getSpawnDate() {
    return spawnDate;
  }

  public void setSpawnDate(Date spawnDate) {
    this.spawnDate = spawnDate;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public Date getDespawnDate() {
    return despawnDate;
  }

  public void setDespawnDate(Date despawnDate) {
    this.despawnDate = despawnDate;
  }

  public List<String> getPlanets() {
    return planets;
  }

  public void setPlanets(List<String> planets) {
    this.planets = planets;
  }

  public Map<String, Integer> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Integer> attributes) {
    this.attributes = attributes;
  }

  public String getResourceTypeString() {
    if (resourceTypeString == null && resourceType != null) {
      return resourceType.getId();
    }
    return resourceTypeString;
  }

  public void setResourceTypeString(String resourceTypeString) {
    this.resourceTypeString = resourceTypeString;
  }

  public int getAttribute(String name) {
    return attributes.get(name);
  }

  public void setAttribute(String attribute, int value) {
    attributes.put(attribute, value);
  }

  @Override
  public String toString() {
    return "GalaxyResource{" + "name='" + name + '\'' + ", resourceType='" + resourceType + '\''
        + ", container='" + container + '\'' + '}';
  }
}
