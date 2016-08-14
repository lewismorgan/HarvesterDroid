package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.FloatTextField;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SchematicDialogController extends VBox implements Initializable {

	private ObservableList<String> availableModifiers = FXCollections.observableArrayList(Attributes.get());

	@FXML
	TextField nameField;
	@FXML
	TextField professionField;
	@FXML
	ListView<String> resourceListView;
	@FXML
	TableView<Schematic.Modifier> attributesTableView;
	@FXML
	ComboBox<String> addModifierComboBox;

	private Schematic schematic;

	public void readSchematic(Schematic schematic) {
		if (schematic == null) // We can't edit a schematic that doesn't exist!
			schematic = new Schematic();
		this.schematic = schematic;

		nameField.textProperty().bindBidirectional(schematic.nameProperty());
		resourceListView.itemsProperty().bindBidirectional(schematic.resourcesProperty());
		professionField.textProperty().bindBidirectional(schematic.groupProperty());
		attributesTableView.itemsProperty().bindBidirectional(schematic.modifiersProperty());

		addModifierComboBox.setItems(availableModifiers);
		addModifierComboBox.getSelectionModel().select(0);

		// Remove modifiers that are being used by the schematic
		schematic.getModifiers().stream()
				.filter(modifier -> availableModifiers.contains(modifier.getName()))
				.forEach(modifier -> availableModifiers.remove(modifier.getName()));

		// listen to property change for already existing entries
		schematic.getModifiers().forEach(modifier -> modifier.nameProperty().addListener((observable, oldValue, newValue) -> {
			if (!oldValue.equals(newValue)) {
				availableModifiers.add(oldValue);
				availableModifiers.remove(newValue);
			}
		}));

		// listener for modifier list that will remove/add modifier names that are used/unused by schematic
		schematic.getModifiers().addListener((ListChangeListener<? super Schematic.Modifier>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					c.getAddedSubList().stream()
							.filter(modifier -> availableModifiers.contains(modifier.getName()))
							.forEach(modifier -> availableModifiers.remove(modifier.getName()));
				} else if (c.wasRemoved()) {
					c.getRemoved().forEach(modifier -> availableModifiers.add(modifier.getName()));
				}
			}
		});
	}

	public void addResource() {
		List<String> choices = new ArrayList<>();
		HarvesterDroid.getCurrentResourcesXml().getTypes().stream().filter(type -> !schematic.getResources().contains(type))
				.forEach(choices::add);
		ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
		dialog.setTitle("Add New Resource");
		dialog.setHeaderText("Select the resource to add to this schematic");
		Optional<String> result = dialog.showAndWait();

		result.ifPresent(choice -> {
			if (!schematic.getResources().contains(choice))
				schematic.getResources().add(choice);
		});
	}

	public void removeResource() {
		String resource = resourceListView.getSelectionModel().getSelectedItem();
		if (resource == null || !schematic.getResources().contains(resource))
			return;

		resourceListView.getSelectionModel().clearSelection();
		schematic.getResources().remove(resource);
	}

	public void addModifier() {
		String modifier = addModifierComboBox.getSelectionModel().getSelectedItem();
		if (modifier == null || modifier.isEmpty())
			return;

		for (Schematic.Modifier existing : schematic.getModifiers()) {
			if (existing.getName().equals(modifier))
				return;
		}

		schematic.getModifiers().add(new Schematic.Modifier(modifier, 0f));
	}

	@SuppressWarnings("unchecked")
	private void createColumns() {
		TableColumn<Schematic.Modifier, String> attributeColumn = new TableColumn<>("Attribute");
		TableColumn<Schematic.Modifier, Float> percentColumn = new TableColumn<>("Value");

		attributeColumn.setOnEditCommit((TableColumn.CellEditEvent<Schematic.Modifier, String> val) -> {
			Optional<Schematic.Modifier> existing = schematic.getModifiers().stream().filter(modifier -> modifier.getName().equals(val.getNewValue())).findFirst();
			if (!existing.isPresent())
				val.getTableView().getItems().get(val.getTablePosition().getRow()).setName(val.getNewValue());
		});

		attributeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		attributeColumn.setCellFactory(param -> new ModifierBoxEditingCell());

		percentColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		percentColumn.setCellFactory(param -> new PercentEditingCell());

		percentColumn.setOnEditCommit(val -> {
			if (!isModifiersAboveCap(val.getTableView().getItems(), val.getNewValue(), val.getRowValue())) {
				val.getTableView().getItems().get(val.getTablePosition().getRow()).setValue(val.getNewValue());
			}
		});

		attributesTableView.getColumns().addAll(attributeColumn, percentColumn);
	}

	private boolean isModifiersAboveCap(List<Schematic.Modifier> modifiers, float newVal, Schematic.Modifier change) {
		float total = (newVal - change.getValue());
		for (Schematic.Modifier modifier : modifiers) {
			total += modifier.getValue();
		}
		return total > 100.0f;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SchematicDialog.setController(this);
		createColumns();
	}

	class PercentEditingCell extends TableCell<Schematic.Modifier, Float> {
		private FloatTextField textField;

		@Override
		public void startEdit() {
			if (isEmpty())
				return;

			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(textField);
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText(String.valueOf(getItem()));
			setGraphic(null);
		}

		@Override
		public void updateItem(Float item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null)
						textField.setText(String.valueOf(item));

					setGraphic(textField);
				} else {
					setText(String.valueOf(item));
					setGraphic(null);
				}
			}
		}

		private void createTextField() {
			textField = new FloatTextField(0, 100, 0);
			textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue)
					return;
				commitEdit(textField.getValue());
			});
			textField.setValue(getItem());
			textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			textField.setOnAction((e) -> commitEdit(textField.getValue()));
		}

	}

	class ModifierBoxEditingCell extends TableCell<Schematic.Modifier, String> {
		private ComboBox<String> comboBox;

		@Override
		public void startEdit() {
			if (isEmpty())
				return;

			super.startEdit();
			createComboBox();
			setText(null);
			setGraphic(comboBox);
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText(getItem());
			setGraphic(null);
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (comboBox != null)
						comboBox.setValue(item);

					setText(item);
					setGraphic(comboBox);
				} else {
					setText(item);
					setGraphic(null);
				}
			}
		}

		private void createComboBox() {
			comboBox = new ComboBox<>(availableModifiers);
			comboBox.valueProperty().set(getItem());
			comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			comboBox.setOnAction((e) -> commitEdit(comboBox.getSelectionModel().getSelectedItem()));
		}
	}

	public Schematic getSchematic() {
		return schematic;
	}
}
