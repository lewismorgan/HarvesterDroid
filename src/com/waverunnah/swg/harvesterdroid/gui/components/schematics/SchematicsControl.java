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

package com.waverunnah.swg.harvesterdroid.gui.components.schematics;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.schematic.SchematicDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Waverunner on 3/20/2017
 */
public class SchematicsControl extends VBox {
    @FXML
    TreeView<SchematicsTreeItem> schematicsTreeView;
    @FXML
    Button removeSchematicButton;
    @FXML
    Button editSchematicButton;

    private ListProperty<Schematic> items = new SimpleListProperty<>();
    private ObjectProperty<Schematic> focusedSchematic = new SimpleObjectProperty<>();

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
        items.addListener((ListChangeListener<Schematic>) c -> {
            while (c.next()) {
                if (c.getAddedSize() > 0) {
                    c.getAddedSubList().forEach(this::createSchematicsTree);
                }
                if (c.getRemovedSize() > 0) {
                    c.getRemoved().forEach(this::removeTreeItem);
                }
            }
        });

        schematicsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.getValue().getIdentifier().isEmpty())
                setFocusedSchematic(newValue);
            else setFocusedSchematic(null);
        });
    }

    private void setup() {
        createListeners();

        removeSchematicButton.disableProperty().bind(Bindings.isNull(focusedSchematic));
        editSchematicButton.disableProperty().bind(Bindings.isNull(focusedSchematic));

        schematicsTreeView.setRoot(createSchematicsTreeItem("root"));
        schematicsTreeView.disableProperty().bind(Bindings.isEmpty(items));
        schematicsTreeView.setCellFactory(param -> new SchematicsTreeCellFactory());
    }

    @FXML
    public void editSelectedSchematic() {
        Schematic selection = getFocusedSchematic();
        if (selection == null)
            displaySchematicDialog();
        else
            displaySchematicDialog(selection);
    }

    @FXML
    public void displaySchematicDialog() {
        SchematicDialog dialog = new SchematicDialog();
        dialog.setTitle("Create Schematic");
        Optional<Schematic> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        Schematic schematic = result.get();
        items.add(schematic);
        schematicsTreeView.getSelectionModel().clearSelection();
        schematicsTreeView.getSelectionModel().selectLast();
    }

    public void displaySchematicDialog(Schematic schematic) {
        SchematicDialog dialog = new SchematicDialog(schematic);
        dialog.setTitle("Edit Schematic");
        Optional<Schematic> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        schematic = result.get();

        updateTreeView();

        schematicsTreeView.getSelectionModel().select(getSchematicsTreeItem(schematicsTreeView.getRoot(), schematic.getId()));
    }

    @FXML
    public void removeSelectedSchematic() {
        Schematic selectedSchematic = getFocusedSchematic();
        if (selectedSchematic == null || !items.contains(selectedSchematic))
            return;

        items.remove(selectedSchematic);
        schematicsTreeView.getSelectionModel().clearSelection();
    }

    private void updateTreeView() {
        schematicsTreeView.getRoot().getChildren().clear();

        for (Schematic item : items) {
            createSchematicsTree(item);
        }
    }

    private TreeItem<SchematicsTreeItem> getSchematicsTreeItem(TreeItem<SchematicsTreeItem> root, String identifier) {
        for (TreeItem<SchematicsTreeItem> treeItem : root.getChildren()) {
            if (treeItem.getValue().getIdentifier().equals(identifier))
                return treeItem;
            if (!treeItem.isLeaf()) {
                TreeItem<SchematicsTreeItem> schematicsTreeItem = getSchematicsTreeItem(treeItem, identifier);
                if (schematicsTreeItem != null)
                    return schematicsTreeItem;
            }
        }
        return null;
    }

    private void createSchematicsTree(Schematic schematic) {
        String[] groups = schematic.getGroup().split(":");
        TreeItem<SchematicsTreeItem> groupTree = getSubGroupTree(schematicsTreeView.getRoot(), groups, 0);

        if (groupTree == null) {
            // Group has never been created, so make it
            TreeItem<SchematicsTreeItem> rootGroup = createGroupTreeItem(groups, 0);
            TreeItem<SchematicsTreeItem> subGroup = null;
            if (groups.length > 1)
                subGroup = getSubGroupTree(rootGroup, groups, 1);

            if (subGroup != null)
                subGroup.getChildren().add(createSchematicsTreeItem(schematic));
            else rootGroup.getChildren().add(createSchematicsTreeItem(schematic));

            schematicsTreeView.getRoot().getChildren().add(rootGroup);
        } else {
            // Base of the group has been created, make the remaining non-existent groups
            TreeItem<SchematicsTreeItem> existingGroup = getParentGroupTreeItem(schematicsTreeView.getRoot(), groups, 0);
            if (existingGroup == groupTree) {
                // Don't need to do anything, all the groups are made, just add the schematic
                existingGroup.getChildren().add(createSchematicsTreeItem(schematic));
            } else {

                int index = getParentGroupIndex(schematicsTreeView.getRoot(), groups, 0);

                TreeItem<SchematicsTreeItem> rootGroup = createGroupTreeItem(groups, index);
                TreeItem<SchematicsTreeItem> subGroup = getSubGroupTree(rootGroup, groups, index);

                if (subGroup != null)
                    subGroup.getChildren().add(createSchematicsTreeItem(schematic));
                else rootGroup.getChildren().add(createSchematicsTreeItem(schematic));

                // Ensure groups remain at the top
                int lastGroupIndex = 0;
                for (int i = 0; i < existingGroup.getChildren().size(); i++) {
                    TreeItem<SchematicsTreeItem> treeItem = existingGroup.getChildren().get(i);
                    if (treeItem.isLeaf())
                        break;
                    else lastGroupIndex++;
                }
                if (!existingGroup.getChildren().contains(rootGroup))
                    existingGroup.getChildren().add(lastGroupIndex, rootGroup);
            }
        }
    }

    private int getParentGroupIndex(TreeItem<SchematicsTreeItem> start, String[] group, int index) {
        if (index >= group.length)
            return index - 1;
        for (TreeItem<SchematicsTreeItem> treeItem : start.getChildren()) {
            if (treeItem.getValue().getName().equals(group[index])) {
                if (treeItem.getValue().isGroup()) {
                    return getParentGroupIndex(treeItem, group, index + 1);
                } else {
                    return index;
                }
            }
        }
        return index;
    }

    private TreeItem<SchematicsTreeItem> getParentGroupTreeItem(TreeItem<SchematicsTreeItem> start, String[] group, int index) {
        if (index >= group.length)
            return start;
        for (TreeItem<SchematicsTreeItem> treeItem : start.getChildren()) {
            if (treeItem.getValue().getName().equals(group[index])) {
                if (treeItem.getValue().isGroup()) {
                    return getParentGroupTreeItem(treeItem, group, index + 1);
                } else {
                    return start;
                }
            }
        }
        return start;
    }

    private TreeItem<SchematicsTreeItem> createSchematicsTreeItem(String name) {
        return new TreeItem<>(new SchematicsTreeItem(name, "", true));
    }

    private TreeItem<SchematicsTreeItem> createSchematicsTreeItem(Schematic schematic) {
        return new TreeItem<>(new SchematicsTreeItem(schematic.getName(), schematic.getId(), false));
    }

    private TreeItem<SchematicsTreeItem> createGroupTreeItem(String[] groups, int index) {
        if (index == (groups.length - 1))
            return createSchematicsTreeItem(groups[index]);

        TreeItem<SchematicsTreeItem> rootGroup = createSchematicsTreeItem(groups[index]);
        TreeItem<SchematicsTreeItem> rootSubGroup = createGroupTreeItem(groups, index + 1);
        rootGroup.getChildren().add(rootSubGroup);

        return rootGroup;
    }

    private void removeTreeItem(Schematic schematic) {
        String[] group = schematic.getGroup().split(":");

        TreeItem<SchematicsTreeItem> groupRoot = getSubGroupTree(schematicsTreeView.getRoot(), group, 0);
        if (groupRoot == null)
            return;

        TreeItem<SchematicsTreeItem> toRemove = null;
        for (TreeItem<SchematicsTreeItem> treeItem : groupRoot.getChildren()) {
            if (treeItem.getValue().getIdentifier().equals(schematic.getId())) {
                toRemove = treeItem;
                break;
            }
        }

        groupRoot.getChildren().remove(toRemove);

        // Try and remove any empty roots
        if (groupRoot.getChildren().size() == 0)
            removeEmptyTreeItems(schematicsTreeView.getRoot(), groupRoot);
    }

    private void removeEmptyTreeItems(TreeItem<SchematicsTreeItem> root, TreeItem<SchematicsTreeItem> target) {
        TreeItem<SchematicsTreeItem> toRemove = getSingleParent(root, target);
        if (root == toRemove) {
            root.getChildren().remove(target);
            return;
        }

        TreeItem<SchematicsTreeItem> parent = toRemove.getParent();

        parent.getChildren().remove(toRemove);

        if (parent.getChildren().size() == 0)
            removeEmptyTreeItems(root, parent);
    }

    private TreeItem<SchematicsTreeItem> getSingleParent(TreeItem<SchematicsTreeItem> root, TreeItem<SchematicsTreeItem> start) {
        if (root == start || root.getChildren().size() > 1 || start.getChildren().size() > 1 || start.getParent().getChildren().size() > 1)
            return start;

        return getSingleParent(root, start.getParent());
    }

    private TreeItem<SchematicsTreeItem> getSubGroupTree(TreeItem<SchematicsTreeItem> start, String[] groups, int index) {
        for (TreeItem<SchematicsTreeItem> treeItem : start.getChildren()) {
            if (treeItem.getValue().getName().equals(groups[index])) {
                if (treeItem.getValue().isGroup()) {
                    // Don't go past the array limit
                    if (index == groups.length - 1)
                        return treeItem;
                    else
                        return getSubGroupTree(treeItem, groups, index + 1);
                }
            }
        }
        return start.getParent();
    }

    public ListProperty<Schematic> itemsProperty() {
        return items;
    }

    public Schematic getFocusedSchematic() {
        return focusedSchematic.get();
    }

    private void setFocusedSchematic(TreeItem<SchematicsTreeItem> selected) {
        if (selected == null) {
            focusedSchematic.set(null);
            return;
        }

        Schematic toSelect = null;

        for (Schematic item : items) {
            if (item.getId().equals(selected.getValue().getIdentifier())) {
                toSelect = item;
                break;
            }
        }

        focusedSchematic.set(toSelect);
    }

    public ObjectProperty<Schematic> focusedSchematicProperty() {
        return focusedSchematic;
    }

    public BooleanProperty disableSchematicsViewProperty() {
        return schematicsTreeView.disableProperty();
    }

}
