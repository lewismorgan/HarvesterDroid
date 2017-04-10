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

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="galaxy_resource") @XmlAccessorType(XmlAccessType.FIELD)
@Entity @Table(name="GalaxyResource")
public class GalaxyResource {
    @Id @Column(name="Name") @XmlAttribute(name="name")
    private String name;
    @Column(name="SpawnDate") @XmlAttribute(name="spawn_date")
    private Date date;
    @Column(name="Container") @XmlAttribute(name="container")
    private String container;
    @Column(name="DespawnDate") @XmlAttribute(name="despawn_date")
    private Date despawnDate;
    @ElementCollection @CollectionTable(name="GalaxyResource_Planets") @Column(name="Planet")
    @XmlElementWrapper(name="planets") @XmlElement(name="planet")
    private List<String> planets;
    @ElementCollection @MapKeyColumn(name="Attribute") @Column(name="Value") @XmlElementWrapper(name="attributes")
    private Map<String, Integer> attributes;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL) @XmlElement(name="resource_type")
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
        if (resourceTypeString == null && resourceType != null)
            return resourceType.getId();
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
