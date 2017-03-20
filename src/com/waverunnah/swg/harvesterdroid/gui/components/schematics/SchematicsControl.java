package com.waverunnah.swg.harvesterdroid.gui.components.schematics;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.SchematicDialog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Waverunner on 3/20/2017
 */
public class SchematicsControl extends VBox {
	@FXML
	ListView<Schematic> schematicsListView;
	@FXML
	ComboBox<String> groupComboBox;
	@FXML
	Button removeSchematicButton;
	@FXML
	Button editSchematicButton;

	private ObservableList<Schematic> items;
	private StringProperty activeGroupProperty = new SimpleStringProperty();

	public SchematicsControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("schematics_control.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		setup();
	}

	private void createListeners() {
		// Select a group if it's added to the list, change selected group if existing was deleted
		groupComboBox.getItems().addListener((ListChangeListener<? super String>) c -> {
			while (c.next()) {
				String selected = groupComboBox.getSelectionModel().getSelectedItem();
				if (c.wasAdded() && selected == null && groupComboBox.getItems().size() > 0) {
					// Nothing was selected previously, time to select it!
					groupComboBox.getSelectionModel().select(0);
				} else if (c.wasRemoved()) {
					boolean oldValueSelected = selected != null && c.getRemoved().contains(selected);
					if (oldValueSelected && groupComboBox.getItems().size() > 0)
						groupComboBox.getSelectionModel().selectFirst();
				}
			}
		});

		// Clear the selected item and jump to the next available item to select
		schematicsListView.getItems().addListener((ListChangeListener<? super Schematic>) c -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					List<? extends Schematic> removed = c.getRemoved();
					int size = schematicsListView.getItems().size();
					int selected = schematicsListView.getSelectionModel().getSelectedIndex();
					if (size != 0 && selected > size) {
						schematicsListView.getSelectionModel().select(selected - removed.size());
					} else {
						// Nothing to select
						schematicsListView.getSelectionModel().clearSelection();
					}
				}
			}
		});

		groupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			activeGroupProperty.set(newValue);
		});

		groupComboBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null)
				return;

			groupComboBox.getSelectionModel().selectFirst();
		});
	}

	private void setup() {
		createListeners();
	}

	@FXML
	public void editSelectedSchematic() {
		if (schematicsListView.getSelectionModel().getSelectedItem() == null)
			displaySchematicDialog();
		else
			displaySchematicDialog(schematicsListView.getSelectionModel().getSelectedItem());
	}

	@FXML
	public void displaySchematicDialog() {
		SchematicDialog dialog = new SchematicDialog();
		dialog.setTitle("Create Schematic");
		Optional<Schematic> result = dialog.showAndWait();
		if (!result.isPresent())
			return;

		Schematic schematic = result.get();
		if (!schematic.isIncomplete())
			items.add(schematic);
	}

	public void displaySchematicDialog(Schematic schematic) {
		SchematicDialog dialog = new SchematicDialog(schematic);
		dialog.setTitle("Edit Schematic");
		Optional<Schematic> result = dialog.showAndWait();
		if (!result.isPresent())
			return;

		Schematic changed = result.get();
		if (changed == schematic && !schematic.isIncomplete()) {
			items.add(schematic);
		} else {
			items.remove(schematic);
			items.add(changed);
		}
	}

	@FXML
	public void removeSelectedSchematic() {
		Schematic selectedSchematic = schematicsListView.getSelectionModel().getSelectedItem();
		if (selectedSchematic == null || !items.contains(selectedSchematic))
			return;

		items.remove(selectedSchematic);
	}

	public ReadOnlyObjectProperty<Schematic> activeSchematicProperty() {
		return schematicsListView.getSelectionModel().selectedItemProperty();
	}

	public ObjectProperty<ObservableList<String>> groupsProperty() {
		return groupComboBox.itemsProperty();
	}

	public StringProperty activeGroupProperty() {
		return activeGroupProperty;
	}

	public void setItems(FilteredList<Schematic> filteredSchematicsList) {
		this.items = (ObservableList<Schematic>) filteredSchematicsList.getSource();
		schematicsListView.setItems(filteredSchematicsList);
	}
}
