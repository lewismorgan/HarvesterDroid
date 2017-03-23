package com.waverunnah.swg.harvesterdroid.data.resources;

import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class GalaxyResource {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();
    private ObjectProperty<ResourceType> resourceType = new SimpleObjectProperty<>();
    private StringProperty container = new SimpleStringProperty();
	private StringProperty despawnDate = new SimpleStringProperty();

	private ObservableList<Planet> planets = FXCollections.observableArrayList();
    private ObservableMap<String, IntegerProperty> attributes;

    private Map<String, Integer> capAttributesMap = new HashMap<>();

    private StringProperty resourceTypeString = new SimpleStringProperty();

	public GalaxyResource() {
		Map<String, IntegerProperty> attributesMap = new HashMap<>(Attributes.size());
		Attributes.forEach((primary, secondary) -> attributesMap.put(primary, new SimpleIntegerProperty(-1)));
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

    public ResourceType getResourceType() {
        return resourceType.get();
    }

    public ObjectProperty<ResourceType> resourceTypeProperty() {
        return resourceType;
    }

    public String getResourceTypeString() {
        return resourceTypeString.get();
    }

    public StringProperty resourceTypeStringProperty() {
        return resourceTypeString;
    }

    public void setResourceTypeString(String resourceTypeString) {
        this.resourceTypeString.set(resourceTypeString);
    }

    public void setResourceType(ResourceType resourceType) {
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

	public String getDespawnDate() {
		return despawnDate.get();
	}

	public StringProperty despawnDateProperty() {
		return despawnDate;
	}

	public void setDespawnDate(String despawnDate) {
		this.despawnDate.set(despawnDate);
	}

	public ObservableMap<String, IntegerProperty> getAttributes() {
		return attributes;
	}

	public void setCapAttribute(String minMaxAttribute, int value) {
	    capAttributesMap.put(minMaxAttribute, value);
	}

    public StringProperty getPercentageCap(String attribute) {
	    attribute = Attributes.getAbbreviation(attribute);
	    if (!capAttributesMap.containsKey(attribute + "max")) {
            return new SimpleStringProperty("--");
        }

	    float max = capAttributesMap.get(attribute + "max");
        if (max <= 0)
            return new SimpleStringProperty("--");

        float min = capAttributesMap.get(attribute + "min");

        float value = getAttribute(Attributes.getFullName(attribute));

        float result = ((value-min)/(max-min))*100;

        StringProperty percentageProperty = new SimpleStringProperty();
        percentageProperty.set("(" + String.valueOf(Math.round(result) + "%" + ")"));
        return percentageProperty;
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

    public void setCapAttributesMap(Map<String, Integer> capAttributesMap) {
        this.capAttributesMap = capAttributesMap;
    }
}
