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

package com.lewisjmorgan.harvesterdroid.app.ui.schematics;

import com.lewisjmorgan.harvesterdroid.app.data.schematics.Schematic;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class SchematicsView implements FxmlView<SchematicsViewModel>, Initializable {

  private static final Logger logger = LogManager.getLogger(SchematicsView.class);

  @FXML
  private Button editSchematicButton;
  @FXML
  private Button removeSchematicButton;
  @FXML
  private TreeView<SchematicsTreeItem> schematicsTreeView;

  @InjectViewModel
  private SchematicsViewModel viewModel;

  public void initialize(URL location, ResourceBundle resources) {
    createListeners();

    schematicsTreeView.setRoot(createSchematicsTreeItem("root"));
    schematicsTreeView.disableProperty().bind(Bindings.isEmpty(schematicsTreeView.getRoot().getChildren()));
    schematicsTreeView.setCellFactory(param -> new SchematicsTreeCellFactory());

    removeSchematicButton.disableProperty().bind(viewModel.getRemoveCommand().executableProperty().not());
    editSchematicButton.disableProperty().bind(viewModel.getEditCommand().executableProperty().not());

    viewModel.subscribe("SchematicUpdated", (s, objects) -> {
      updateTreeView();
      if (objects != null && objects instanceof Schematic[]) {
        viewModel.setSelected((Schematic) objects[0]);
      }
    });

    updateTreeView();
  }

  private void createListeners() {
    viewModel.schematicsProperty().addListener((ListChangeListener<Schematic>) c -> {
      while (c.next()) {
        if (c.getAddedSize() > 0) {
          c.getAddedSubList().forEach(this::createSchematicsTree);
          schematicsTreeView.getSelectionModel().selectLast();
        }
        if (c.getRemovedSize() > 0) {
          c.getRemoved().forEach(this::removeTreeItem);
          schematicsTreeView.getSelectionModel().selectLast();
        }
      }
    });

    viewModel.selectedProperty().addListener(((observable, oldValue, newValue) -> onSchematicSelected(newValue)));

    schematicsTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !newValue.getValue().getIdentifier().isEmpty()) {
        onSchematicTreeItemSelected(newValue);
      } else {
        onSchematicTreeItemSelected(null);
      }
    });
  }

  public void removeSelectedSchematic() {
    viewModel.getRemoveCommand().execute();
  }

  public void editSelectedSchematic() {
    viewModel.getEditCommand().execute();
  }

  public void displaySchematicDialog() {
    viewModel.getAddCommand().execute();
  }

  private void onSchematicSelected(Schematic schematic) {
    if (schematic == null) {
      return;
    }

    TreeItem<SchematicsTreeItem> toSelect = getSchematicsTreeItem(schematicsTreeView.getRoot(), schematic.getId());
    if (schematicsTreeView.getSelectionModel().getSelectedItem() != toSelect) {
      schematicsTreeView.getSelectionModel().select(toSelect);
    }
  }

  private void onSchematicTreeItemSelected(TreeItem<SchematicsTreeItem> selected) {
    if (selected == null) {
      viewModel.setSelected(null);
      return;
    }

    Schematic toSelect = null;

    for (Schematic item : viewModel.getSchematics()) {
      if (item.getId().equals(selected.getValue().getIdentifier())) {
        toSelect = item;
        break;
      }
    }

    viewModel.setSelected(toSelect);
  }

  private void updateTreeView() {
    schematicsTreeView.getRoot().getChildren().clear();

    for (Schematic item : viewModel.schematicsProperty()) {
      createSchematicsTree(item);
    }
  }

  private TreeItem<SchematicsTreeItem> getSchematicsTreeItem(TreeItem<SchematicsTreeItem> root, String identifier) {
    for (TreeItem<SchematicsTreeItem> treeItem : root.getChildren()) {
      if (treeItem.getValue().getIdentifier().equals(identifier)) {
        return treeItem;
      }
      if (!treeItem.isLeaf()) {
        TreeItem<SchematicsTreeItem> schematicsTreeItem = getSchematicsTreeItem(treeItem, identifier);
        if (schematicsTreeItem != null) {
          return schematicsTreeItem;
        }
      }
    }
    return null;
  }

  private void createSchematicsTree(Schematic schematic) {
    String[] groups = schematic.getGroup().split(":");
    if (groups.length == 0 || (groups.length == 1 && (groups[0] == null || groups[0].isEmpty()))) {
      schematicsTreeView.getRoot().getChildren().add(createSchematicsTreeItem(schematic));
      return;
    }

    TreeItem<SchematicsTreeItem> groupTree = getSubGroupTree(schematicsTreeView.getRoot(), groups, 0);
    if (groupTree == null) {
      logger.debug("Creating group hierarchy {} ", schematic.getGroup());
      TreeItem<SchematicsTreeItem> rootGroup = createGroupTreeItem(groups, 0);
      TreeItem<SchematicsTreeItem> subGroup = null;

      if (groups.length > 1) {
        subGroup = getSubGroupTree(rootGroup, groups, 1);
      }

      if (subGroup != null) {
        subGroup.getChildren().add(createSchematicsTreeItem(schematic));
        logger.debug("Created schematic {} on sub group {}", schematic.getName(), subGroup.getValue().getName());
      } else {
        rootGroup.getChildren().add(createSchematicsTreeItem(schematic));
        logger.debug("Created schematic {} on root group {}", schematic.getName(), rootGroup.getValue().getName());
      }

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
        // Already at the first index, traverse deeper to get the sub group tree
        TreeItem<SchematicsTreeItem> subGroup = getSubGroupTree(rootGroup, groups, index + 1);

        if (subGroup != null) {
          subGroup.getChildren().add(createSchematicsTreeItem(schematic));
          logger.debug("Created sub group {} and added schematic {}", subGroup.getValue(), schematic.getName());
        } else {
          // This shouldn't happen, but if it does...
          rootGroup.getChildren().add(createSchematicsTreeItem(schematic));
          logger.warn("Null subgroup, adding {} to rootgroup {} ", schematic.getName(), rootGroup.getValue());
        }

        // Ensure groups remain at the top
        int lastGroupIndex = 0;
        for (int i = 0; i < existingGroup.getChildren().size(); i++) {
          TreeItem<SchematicsTreeItem> treeItem = existingGroup.getChildren().get(i);
          if (treeItem.isLeaf()) {
            break;
          } else {
            lastGroupIndex++;
          }
        }
        if (!existingGroup.getChildren().contains(rootGroup)) {
          existingGroup.getChildren().add(lastGroupIndex, rootGroup);
        }
      }
    }
  }

  private int getParentGroupIndex(TreeItem<SchematicsTreeItem> start, String[] group, int index) {
    if (index >= group.length) {
      return index - 1;
    }
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
    if (index >= group.length) {
      return start;
    }
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
    String name = schematic.getName();
    if (name == null || name.isEmpty()) {
      name = "Unnamed";
    }
    return new TreeItem<>(new SchematicsTreeItem(name, schematic.getId(), false));
  }

  private TreeItem<SchematicsTreeItem> createGroupTreeItem(String[] groups, int index) {
    if (index == (groups.length - 1)) {
      return createSchematicsTreeItem(groups[index]);
    }

    TreeItem<SchematicsTreeItem> rootGroup = createSchematicsTreeItem(groups[index]);
    TreeItem<SchematicsTreeItem> rootSubGroup = createGroupTreeItem(groups, index + 1);
    rootGroup.getChildren().add(rootSubGroup);

    return rootGroup;
  }

  private void removeTreeItem(Schematic schematic) {
    String[] group = schematic.getGroup().split(":");

    TreeItem<SchematicsTreeItem> groupRoot = getSubGroupTree(schematicsTreeView.getRoot(), group, 0);
    if (groupRoot == null && group.length == 0 || (group.length == 1 && (group[0] == null || group[0].isEmpty()))) {
      groupRoot = schematicsTreeView.getRoot();
    }
    if (groupRoot == null) {
      return;
    }

    TreeItem<SchematicsTreeItem> toRemove = null;
    for (TreeItem<SchematicsTreeItem> treeItem : groupRoot.getChildren()) {
      if (treeItem.getValue().getIdentifier().equals(schematic.getId())) {
        toRemove = treeItem;
        break;
      }
    }

    groupRoot.getChildren().remove(toRemove);

    // Try and remove any empty roots
    if (groupRoot.getChildren().size() == 0) {
      removeEmptyTreeItems(schematicsTreeView.getRoot(), groupRoot);
    }
  }

  private void removeEmptyTreeItems(TreeItem<SchematicsTreeItem> root, TreeItem<SchematicsTreeItem> target) {
    TreeItem<SchematicsTreeItem> toRemove = getSingleParent(root, target);
    if (root == toRemove) {
      root.getChildren().remove(target);
      return;
    }

    TreeItem<SchematicsTreeItem> parent = toRemove.getParent();

    parent.getChildren().remove(toRemove);

    if (parent.getChildren().size() == 0) {
      removeEmptyTreeItems(root, parent);
    }
  }

  private TreeItem<SchematicsTreeItem> getSingleParent(TreeItem<SchematicsTreeItem> root, TreeItem<SchematicsTreeItem> start) {
    if (root == start || root.getChildren().size() > 1 || start.getChildren().size() > 1 || start.getParent().getChildren().size() > 1) {
      return start;
    }

    return getSingleParent(root, start.getParent());
  }

  private TreeItem<SchematicsTreeItem> getSubGroupTree(TreeItem<SchematicsTreeItem> start, String[] groups, int index) {
    for (TreeItem<SchematicsTreeItem> treeItem : start.getChildren()) {
      if (treeItem.getValue().getName().equals(groups[index])) {
        if (treeItem.getValue().isGroup()) {
          // Don't go past the array limit
          if (index == groups.length - 1) {
            return treeItem;
          } else {
            return getSubGroupTree(treeItem, groups, index + 1);
          }
        }
      }
    }
    return start.getParent();
  }
}