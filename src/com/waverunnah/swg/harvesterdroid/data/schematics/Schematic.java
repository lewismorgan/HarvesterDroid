package com.waverunnah.swg.harvesterdroid.data.schematics;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.UUID;

public class Schematic {
	private StringProperty name = new SimpleStringProperty();
	private StringProperty group = new SimpleStringProperty();
	private ListProperty<String> resources = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ListProperty<Modifier> modifiers = new SimpleListProperty<>(FXCollections.observableArrayList());
	private MapProperty<String, String> resourceWeights = new SimpleMapProperty<>(FXCollections.emptyObservableMap());

	private String identifier = UUID.randomUUID().toString();

	public Schematic() {

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

    public ObservableMap<String, String> getResourceWeights() {
        return resourceWeights.get();
    }

    public MapProperty<String, String> resourceWeightsProperty() {
        return resourceWeights;
    }

    public String getIdentifier() {
        return identifier;
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
