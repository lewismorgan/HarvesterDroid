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

import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GalaxyResource {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();
    private ObjectProperty<ResourceType> resourceType = new SimpleObjectProperty<>();
    private StringProperty container = new SimpleStringProperty();
    private StringProperty despawnDate = new SimpleStringProperty();

    private ObservableList<Planet> planets = FXCollections.observableArrayList();
    private ObservableMap<String, IntegerProperty> attributes;

    private StringProperty resourceTypeString = new SimpleStringProperty();

    public GalaxyResource() {
        Map<String, IntegerProperty> attributesMap = new HashMap<>(Attributes.size());
        Attributes.forEach((primary, secondary) -> attributesMap.put(primary, new SimpleIntegerProperty(-1)));
        attributes = FXCollections.observableMap(attributesMap);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public StringProperty dateProperty() {
        return date;
    }

    public ResourceType getResourceType() {
        return resourceType.get();
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType.set(resourceType);
    }

    public ObjectProperty<ResourceType> resourceTypeProperty() {
        return resourceType;
    }

    public String getResourceTypeString() {
        return resourceTypeString.get();
    }

    public void setResourceTypeString(String resourceTypeString) {
        this.resourceTypeString.set(resourceTypeString);
    }

    public StringProperty resourceTypeStringProperty() {
        return resourceTypeString;
    }

    public String getContainer() {
        return container.get();
    }

    public void setContainer(String container) {
        this.container.set(container);
    }

    public StringProperty containerProperty() {
        return container;
    }

    public String getDespawnDate() {
        return despawnDate.get();
    }

    public void setDespawnDate(String despawnDate) {
        this.despawnDate.set(despawnDate);
    }

    public StringProperty despawnDateProperty() {
        return despawnDate;
    }

    public ObservableMap<String, IntegerProperty> getAttributes() {
        return attributes;
    }

    public void setAttribute(String attribute, int value) {
        IntegerProperty property = attributes.get(attribute);
        if (property != null)
            property.set(value);
        else attributes.put(attribute, new SimpleIntegerProperty(value));
    }

    public ObservableList<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(Collection<Planet> planets) {
        this.planets.clear();
        this.planets.addAll(planets);
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
        return attributes.get(name).getValue();
    }
}
