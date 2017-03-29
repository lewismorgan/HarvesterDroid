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

package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.IntegerTextField;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SchematicDialogController extends VBox implements Initializable {

    @FXML
    TextField nameField;
    @FXML
    TextField groupField;
    @FXML
    ListView<String> resourceListView;
    @FXML
    TableView<Schematic.Modifier> attributesTableView;
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
    private Map<String, String> resourceTypes;
    private ObjectProperty<Schematic> schematic = new SimpleObjectProperty<>(null);

    public void readSchematic(Schematic schematic) {
        if (schematic == null)
            return;
        this.schematic.set(schematic);

        nameField.textProperty().bindBidirectional(schematic.nameProperty());
        resourceListView.itemsProperty().bindBidirectional(schematic.resourcesProperty());
        groupField.textProperty().bindBidirectional(schematic.groupProperty());
        attributesTableView.itemsProperty().bindBidirectional(schematic.modifiersProperty());

        schematic.getModifiers().addListener((ListChangeListener<? super Schematic.Modifier>) c -> {
            while (c.next()) {
                filterModifiers((ObservableList<Schematic.Modifier>) c.getList());
            }
        });

        filterModifiers(schematic.getModifiers());
    }

    private void filterModifiers(ObservableList<Schematic.Modifier> modifiers) {
        addModifierComboBox.getSelectionModel().clearSelection();
        availableModifiers.setPredicate(avail -> {
            for (Schematic.Modifier modifier : modifiers) {
                if (modifier.getName().equals(avail))
                    return false;
            }
            return true;
        });
        addModifierComboBox.getSelectionModel().selectFirst();
    }

    public void addResource() {
        Schematic schematic = getSchematic();

        Map<String, String> choices = new HashMap<>();
        resourceTypes.keySet().stream().filter(type -> !schematic.getResources().contains(type))
                .forEach(choice -> choices.put(resourceTypes.get(choice), choice));
        AddResourceTypeDialog dialog = new AddResourceTypeDialog(choices);

        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(selection -> selection.forEach(type -> {
            if (!schematic.getResources().contains(type))
                schematic.getResources().add(type);
        }));
    }

    public void removeResource() {
        Schematic schematic = getSchematic();

        String resource = resourceListView.getSelectionModel().getSelectedItem();
        if (resource == null || !schematic.getResources().contains(resource))
            return;

        resourceListView.getSelectionModel().clearSelection();
        schematic.getResources().remove(resource);
    }

    public void addAttribute() {
        Schematic schematic = getSchematic();
        if (schematic == null)
            return;

        String modifier = addModifierComboBox.getSelectionModel().getSelectedItem();
        if (modifier == null || modifier.isEmpty())
            return;

        int total = 0;
        for (Schematic.Modifier existing : schematic.getModifiers()) {
            if (existing.getName().equals(modifier)) {
                return;
            }
            total += existing.getValue();
        }

        // divide last mod by number mods so total always = 100
        if (total >= 100) {
            Schematic.Modifier lastMod = schematic.getModifiers().get(schematic.getModifiers().size() - 1);
            int oldValue = lastMod.getValue();
            total -= oldValue;
            lastMod.setValue(Math.round(lastMod.getValue() / (schematic.getModifiers().size() + 1)));
            total += lastMod.getValue();
        }

        schematic.getModifiers().add(new Schematic.Modifier(modifier, 100 - total));
    }

    public void removeAttribute() {
        Schematic schematic = getSchematic();
        if (schematic == null)
            return;

        Schematic.Modifier selected = attributesTableView.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        if (schematic.getModifiers().size() > 1) {
            Schematic.Modifier mod = null;
            for (Schematic.Modifier modifier : schematic.getModifiers()) {
                if (modifier != selected) {
                    mod = modifier;
                    break;
                }
            }
            if (mod != null)
                mod.setValue(mod.getValue() + selected.getValue());
        }

        schematic.getModifiers().remove(selected);
    }

    @SuppressWarnings("unchecked")
    private void createColumns() {
        TableColumn<Schematic.Modifier, String> attributeColumn = new TableColumn<>("Attribute");
        TableColumn<Schematic.Modifier, Integer> valueColumn = new TableColumn<>("Value");

        attributeColumn.setOnEditCommit((TableColumn.CellEditEvent<Schematic.Modifier, String> val) -> {
            Optional<Schematic.Modifier> existing = getSchematic().getModifiers().stream().filter(modifier -> modifier.getName().equals(val.getNewValue())).findFirst();
            if (!existing.isPresent()) {
                val.getTableView().getItems().get(val.getTablePosition().getRow()).setName(val.getNewValue());
                filterModifiers(val.getTableView().getItems());
            }
        });

        attributeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        attributeColumn.setCellFactory(param -> new ModifierBoxEditingCell());

        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(param -> new PercentEditingCell());

        valueColumn.setOnEditCommit(val -> {
            if (isModifiersBetweenCap(val.getTableView().getItems(), val.getNewValue(), val.getRowValue())) {
                val.getTableView().getItems().get(val.getTablePosition().getRow()).setValue(val.getNewValue());
            }
        });

        attributesTableView.getColumns().addAll(attributeColumn, valueColumn);
    }

    private boolean isModifiersBetweenCap(List<Schematic.Modifier> modifiers, int newVal, Schematic.Modifier change) {
        int total = (newVal - change.getValue());
        for (Schematic.Modifier modifier : modifiers) {
            total += modifier.getValue();
        }
        return total >= 100 || total <= 100;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceTypes = Launcher.getResourceTypes();
        SchematicDialog.setController(this);

        createAttributesTable();

        removeAttributeButton.disableProperty().bind(Bindings.isEmpty(attributesTableView.getSelectionModel().getSelectedItems()));
        addAttributeButton.disableProperty().bind(Bindings.isEmpty(availableModifiers));

        addModifierComboBox.setItems(availableModifiers);
        addModifierComboBox.disableProperty().bind(Bindings.isEmpty(availableModifiers));
        addModifierComboBox.getSelectionModel().select(0);

        removeResourceButton.disableProperty().bind(Bindings.isEmpty(resourceListView.getSelectionModel().getSelectedItems()));

        addModifierComboBox.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else setText(Attributes.getLocalizedName(item));
            }
        });
        addModifierComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else setText(Attributes.getLocalizedName(item));
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
    }

    private void createAttributesTable() {
        createColumns();

        attributesTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;

            TablePosition focusedCellPosition = attributesTableView.getFocusModel().getFocusedCell();
            attributesTableView.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
        });
    }

    public Schematic getSchematic() {
        return schematic.get();
    }

    class PercentEditingCell extends TableCell<Schematic.Modifier, Integer> {
        private IntegerTextField textField;

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
        public void updateItem(Integer item, boolean empty) {
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
            textField = new IntegerTextField(0, 100, 0);
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

}
