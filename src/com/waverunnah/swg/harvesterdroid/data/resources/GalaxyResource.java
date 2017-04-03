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

package com.waverunnah.swg.harvesterdroid.data.resources;

import com.waverunnah.swg.harvesterdroid.app.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalaxyResource {
    private String name;
    private String date;
    private ResourceType resourceType;
    private String container;
    private String despawnDate;

    private List<Planet> planets;
    private Map<String, Integer> attributes;

    private String resourceTypeString;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDespawnDate() {
        return despawnDate;
    }

    public void setDespawnDate(String despawnDate) {
        this.despawnDate = despawnDate;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(List<Planet> planets) {
        this.planets = planets;
    }

    public Map<String, Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Integer> attributes) {
        this.attributes = attributes;
    }

    public String getResourceTypeString() {
        return resourceTypeString;
    }

    public void setResourceTypeString(String resourceTypeString) {
        this.resourceTypeString = resourceTypeString;
    }

    public void setAttribute(String attribute, int value) {
        attributes.put(attribute, value);
    }


    @Override
    public String toString() {
        return "GalaxyResource{" +
                "name='" + name + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", container='" + container + '\'' +
                '}';
    }

    public int getAttribute(String name) {
        return attributes.get(name);
    }
}
