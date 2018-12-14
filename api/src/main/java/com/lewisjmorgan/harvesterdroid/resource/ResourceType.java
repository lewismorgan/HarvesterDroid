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

package com.lewisjmorgan.harvesterdroid.resource;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Waverunner on 3/23/2017.
 */
//@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resource_type")
@Entity
public class ResourceType {

  @Id
  @XmlAttribute(name = "id")
  private String id;
  @XmlAttribute(name = "name")
  private String name;
  @XmlElementWrapper(name = "min_max")
  private Map<String, Integer> minMaxMap;

  public ResourceType() {
    minMaxMap = new HashMap<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Integer> getMinMaxMap() {
    return minMaxMap;
  }

  public void setMinMaxMap(Map<String, Integer> minMaxMap) {
    this.minMaxMap = minMaxMap;
  }

  @Override
  public String toString() {
    return "ResourceType{"
        + "id='" + id + '\''
        + ", name='" + name + '\''
        + '}';
  }
}
