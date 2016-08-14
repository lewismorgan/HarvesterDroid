package com.waverunnah.swg.harvesterdroid.data.resources;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class GalaxyResource {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();
    private StringProperty resourceType = new SimpleStringProperty();
    private StringProperty container = new SimpleStringProperty();

    private ObservableList<Planet> planets = FXCollections.observableArrayList();
    private ObservableMap<String, IntegerProperty> attributes;

	public GalaxyResource() {
		Map<String, IntegerProperty> attributesMap = new HashMap<>(HarvesterDroid.modifiers.size());
		HarvesterDroid.modifiers.forEach(modifier -> {
			attributesMap.put(modifier, new SimpleIntegerProperty(-1));
		});
		attributes = FXCollections.observableMap(attributesMap);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getDate() {
		return date.get();
	}

	public StringProperty dateProperty() {
		return date;
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getResourceType() {
		return resourceType.get();
	}

	public StringProperty resourceTypeProperty() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType.set(resourceType);
	}

	public String getContainer() {
		return container.get();
	}

	public StringProperty containerProperty() {
		return container;
	}

	public void setContainer(String container) {
		this.container.set(container);
	}

	public ObservableMap<String, IntegerProperty> getAttributes() {
		return attributes;
	}

	public void setAttribute(String attribute, int value) {
		IntegerProperty property = attributes.get(attribute);
		property.set(value);
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
