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

package com.waverunnah.swg.harvesterdroid.data.schematics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@XmlRootElement(name = "schematic")
@XmlAccessorType(XmlAccessType.FIELD)
public class Schematic {
    private final transient String id;

    @XmlAttribute
    private String group;
    @XmlAttribute
    private String name;

    @XmlElementWrapper(name = "resources")
    @XmlElement(name = "resource")
    private List<String> resources;

    @XmlElementWrapper(name = "modifiers")
    @XmlElement(name = "modifier")
    private Map<String, Integer> modifiers;

    public Schematic() {
        this.id = UUID.randomUUID().toString();
        this.group = "";
        this.name = "";
        this.resources = new ArrayList<>();
        this.modifiers = new HashMap<>();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getId() {
        return id;
    }

    public Map<String, Integer> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Map<String, Integer> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String toString() {
        return "Schematic{" +
                "id='" + id + '\'' +
                ", group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", resources=" + resources +
                ", modifiers=" + modifiers +
                '}';
    }
}
