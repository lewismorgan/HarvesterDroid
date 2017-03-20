package com.waverunnah.swg.harvesterdroid.data.schematics;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Schematic {

	private StringProperty name = new SimpleStringProperty();
	private StringProperty group = new SimpleStringProperty();
	private ListProperty<String> resources = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ListProperty<Modifier> modifiers = new SimpleListProperty<>(FXCollections.observableArrayList());

	public static Schematic getDefault() {
		Schematic schematic = new Schematic();
		schematic.setName("Default");
		schematic.setGroup("Default");
		schematic.getResources().add("iron");
		schematic.getModifiers().add(new Modifier("overall_quality", 33));
		return schematic;
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

	public boolean isIncomplete() {
		return getName() == null || getGroup() == null || getResources() == null || getModifiers() == null
				|| getResources().isEmpty() || getModifiers().isEmpty();
	}

	@Override
	public String toString() {
		return getName();
	}

	public static class Modifier {
		private StringProperty name = new SimpleStringProperty();
		private IntegerProperty value = new SimpleIntegerProperty();

		public Modifier(String name, int value) {
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

		public int getValue() {
			return value.get();
		}

		public IntegerProperty valueProperty() {
			return value;
		}

		public void setValue(int value) {
			this.value.set(value);
		}
	}
}
