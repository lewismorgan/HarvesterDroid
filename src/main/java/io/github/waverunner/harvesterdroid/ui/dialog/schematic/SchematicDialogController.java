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

package io.github.waverunner.harvesterdroid.ui.dialog.schematic;

import io.github.waverunner.harvesterdroid.Launcher;
import io.github.waverunner.harvesterdroid.api.resource.Attributes;
import io.github.waverunner.harvesterdroid.data.schematics.Schematic;
import io.github.waverunner.harvesterdroid.ui.IntegerTextField;
import io.github.waverunner.harvesterdroid.ui.dialog.AddResourceTypeDialog;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class SchematicDialogController extends VBox implements Initializable {
  @FXML
  TextField nameField;
  @FXML
  TextField groupField;
  @FXML
  ListView<String> resourceListView;
  @FXML
  TableView<Modifier> attributesTableView;
  @FXML
  ComboBox<String> addModifierComboBox;
  @FXML
  TitledPane attributesPane;
  @FXML
  TitledPane resourcesPane;
  @FXML
  Button addAttributeButton;
  @FXML
  Button removeAttributeButton;
  @FXML
  Button removeResourceButton;

  private FilteredList<String> availableModifiers = new FilteredList<>(FXCollections.observableArrayList(Attributes.get()));
  private ListProperty<Modifier> modifiers = new SimpleListProperty<>(FXCollections.observableArrayList());
  private ListProperty<String> resources = new SimpleListProperty<>(FXCollections.observableArrayList());
  private Map<String, String> resourceTypes;

  private Schematic schematic;

  public void readSchematic(Schematic schematic) {
    if (schematic == null) {
      return;
    }

    this.schematic = schematic;

    nameField.setText(schematic.getName());
    groupField.setText(schematic.getGroup());
    resources.addAll(schematic.getResources());

    schematic.getModifiers().forEach((s, integer) -> modifiers.add(new Modifier(s, integer)));
  }

  private void refreshModifiers() {
    addModifierComboBox.getSelectionModel().clearSelection();
    availableModifiers.setPredicate(avail -> {
      for (Modifier modifier : modifiers) {
        if (modifier.getName().equals(avail)) {
          return false;
        }
      }
      return true;
    });
    addModifierComboBox.getSelectionModel().selectFirst();
  }

  public void addResource() {
    Map<String, String> choices = new HashMap<>();
    resourceTypes.keySet().stream().filter(type -> !resources.contains(type))
        .forEach(choice -> choices.put(resourceTypes.get(choice), choice));
    AddResourceTypeDialog dialog = new AddResourceTypeDialog(choices);

    Optional<List<String>> result = dialog.showAndWait();
    result.ifPresent(selection -> selection.forEach(type -> {
      if (!resources.contains(type)) {
        resources.add(type);
      }
    }));
  }

  public void removeResource() {
    String resource = resourceListView.getSelectionModel().getSelectedItem();
    if (resource == null || !resources.contains(resource)) {
      return;
    }

    resourceListView.getSelectionModel().clearSelection();
    resources.remove(resource);
  }

  public void addAttribute() {
    String modifier = addModifierComboBox.getSelectionModel().getSelectedItem();
    if (modifier == null || modifier.isEmpty()) {
      return;
    }

    int total = 0;
    for (Modifier existing : modifiers) {
      if (existing.getName().equals(modifier)) {
        return;
      }
      total += existing.getValue();
    }

    // divide last mod by number mods so total always = 100
    if (total >= 100) {
      Modifier lastMod = modifiers.get(modifiers.size() - 1);
      int oldValue = lastMod.getValue();
      total -= oldValue;
      lastMod.setValue(Math.round(lastMod.getValue() / (modifiers.size() + 1)));
      total += lastMod.getValue();
    }

    modifiers.add(new Modifier(modifier, 100 - total));
  }

  public void removeAttribute() {
    Modifier selected = attributesTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    if (modifiers.size() > 1) {
      Modifier mod = null;
      for (Modifier modifier : modifiers) {
        if (modifier != selected) {
          mod = modifier;
          break;
        }
      }
      if (mod != null) {
        mod.setValue(mod.getValue() + selected.getValue());
      }
    }

    modifiers.remove(selected);
  }

  @SuppressWarnings("unchecked")
  private void createColumns() {
    TableColumn<Modifier, String> attributeColumn = new TableColumn<>("Attribute");

    attributeColumn.setOnEditCommit((TableColumn.CellEditEvent<Modifier, String> val) -> {
      Optional<Modifier> existing = modifiers.stream().filter(modifier -> modifier.getName().equals(val.getNewValue())).findFirst();
      if (!existing.isPresent()) {
        val.getTableView().getItems().get(val.getTablePosition().getRow()).setName(val.getNewValue());
        refreshModifiers();
      }
    });

    attributeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    attributeColumn.setCellFactory(param -> new ModifierBoxEditingCell());

    TableColumn<Modifier, Integer> valueColumn = new TableColumn<>("Value");

    valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    valueColumn.setCellFactory(param -> new PercentEditingCell());

    valueColumn.setOnEditCommit(val -> {
      if (isModifiersBetweenCap(val.getTableView().getItems(), val.getNewValue(), val.getRowValue())) {
        val.getTableView().getItems().get(val.getTablePosition().getRow()).setValue(val.getNewValue());
      }
    });

    attributesTableView.getColumns().addAll(attributeColumn, valueColumn);
  }

  private boolean isModifiersBetweenCap(List<Modifier> modifiers, int newVal, Modifier change) {
    int total = (newVal - change.getValue());
    for (Modifier modifier : modifiers) {
      total += modifier.getValue();
    }
    return total >= 100 || total <= 100;
  }

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    resourceTypes = Launcher.getResourceTypes();
    SchematicDialog.setController(this);

    createAttributesTable();

    removeAttributeButton.disableProperty().bind(Bindings.isEmpty(attributesTableView.getSelectionModel().getSelectedItems()));
    addAttributeButton.disableProperty().bind(Bindings.isEmpty(availableModifiers));

    addModifierComboBox.setItems(availableModifiers);
    addModifierComboBox.disableProperty().bind(Bindings.isEmpty(availableModifiers));
    addModifierComboBox.getSelectionModel().select(0);

    removeResourceButton.disableProperty().bind(Bindings.isEmpty(resourceListView.getSelectionModel().getSelectedItems()));

    resourceListView.itemsProperty().bind(resources);

    addModifierComboBox.setCellFactory(param -> new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
        } else {
          setText(Attributes.getLocalizedName(item));
        }
      }
    });
    addModifierComboBox.setButtonCell(new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
        } else {
          setText(Attributes.getLocalizedName(item));
        }
      }
    });

    resourceListView.setCellFactory(param -> new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          setText(resourceTypes.get(item));
        } else {
          setText(null);
        }
      }
    });

    modifiers.addListener((ListChangeListener<Modifier>) c -> {
      while (c.next()) {
        refreshModifiers();
      }
    });
  }

  private void createAttributesTable() {
    createColumns();

    attributesTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getButton() != MouseButton.PRIMARY) {
        return;
      }

      TablePosition focusedCellPosition = attributesTableView.getFocusModel().getFocusedCell();
      attributesTableView.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
    });

    attributesTableView.itemsProperty().bind(modifiers);
  }

  public Schematic createSchematic() {
    if (schematic == null) {
      schematic = new Schematic();
    }

    schematic.setName(nameField.getText());
    schematic.setGroup(groupField.getText());
    schematic.setResources(resources.get());
    Map<String, Integer> modifiers = new HashMap<>();
    this.modifiers.get().forEach(modifier -> modifiers.put(modifier.getName(), modifier.getValue()));
    schematic.setModifiers(modifiers);

    return schematic;
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

    public void setName(String name) {
      this.name.set(name);
    }

    public StringProperty nameProperty() {
      return name;
    }

    public int getValue() {
      return value.get();
    }

    public void setValue(int value) {
      this.value.set(value);
    }

    public IntegerProperty valueProperty() {
      return value;
    }
  }

  class PercentEditingCell extends TableCell<Modifier, Integer> {
    private IntegerTextField textField;

    @Override
    public void startEdit() {
      if (isEmpty()) {
        return;
      }

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
    public void updateItem(Integer item, boolean empty) {
      super.updateItem(item, empty);

      if (empty) {
        setText(null);
        setGraphic(null);
      } else {
        if (isEditing()) {
          if (textField != null) {
            textField.setText(String.valueOf(item));
          }

          setGraphic(textField);
        } else {
          setText(String.valueOf(item));
          setGraphic(null);
        }
      }
    }

    private void createTextField() {
      textField = new IntegerTextField(0, 100, 0);
      textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
          return;
        }
        commitEdit(textField.getValue());
      });
      textField.setValue(getItem());
      textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
      textField.setOnAction((e) -> commitEdit(textField.getValue()));
    }

  }

  class ModifierBoxEditingCell extends TableCell<Modifier, String> {
    private ComboBox<String> comboBox;

    @Override
    public void startEdit() {
      if (isEmpty()) {
        return;
      }

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
          if (comboBox != null) {
            comboBox.setValue(item);
          }

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
}
