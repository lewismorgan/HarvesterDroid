package com.waverunnah.swg.harvesterdroid.data.schematics;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Schematic {

	private StringProperty name = new SimpleStringProperty();
	private StringProperty group = new SimpleStringProperty();
	private ObservableMap<String, Integer> resources = FXCollections.observableHashMap();
	private ObservableMap<String, Float> modifiers = FXCollections.observableHashMap();

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getGroup() {
		return group.get();
	}

	public StringProperty groupProperty() {
		return group;
	}

	public void setGroup(String group) {
		this.group.set(group);
	}

	public ObservableMap<String, Integer> getResources() {
		return resources;
	}

	public void setResources(ObservableMap<String, Integer> resources) {
		this.resources = resources;
	}

	public ObservableMap<String, Float> getModifiers() {
		return modifiers;
	}

	public void setModifiers(ObservableMap<String, Float> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public String toString() {
		return getName();
	}
}
