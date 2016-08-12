package com.waverunnah.swg.harvesterdroid.data.schematics;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

public class Schematic {

	private StringProperty name = new SimpleStringProperty();
	private StringProperty group = new SimpleStringProperty();
	private ListProperty<String> resources = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ListProperty<Modifier> modifiers = new SimpleListProperty<>(FXCollections.observableArrayList());

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

	public ObservableList<String> getResources() {
		return resources.get();
	}

	public ListProperty<String> resourcesProperty() {
		return resources;
	}

	public void setResources(ObservableList<String> resources) {
		this.resources.set(resources);
	}

	public ObservableList<Modifier> getModifiers() {
		return modifiers.get();
	}

	public ListProperty<Modifier> modifiersProperty() {
		return modifiers;
	}

	public void setModifiers(ObservableList<Modifier> modifiers) {
		this.modifiers.set(modifiers);
	}

	@Override
	public String toString() {
		return getName();
	}

	public static class Modifier {
		private StringProperty name = new SimpleStringProperty();
		private FloatProperty value = new SimpleFloatProperty();

		public Modifier(String name, float value) {
			setName(name);
			setValue(value);
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

		public float getValue() {
			return value.get();
		}

		public FloatProperty valueProperty() {
			return value;
		}

		public void setValue(float value) {
			this.value.set(value);
		}
	}
}
