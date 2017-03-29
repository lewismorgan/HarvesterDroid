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

package com.waverunnah.swg.harvesterdroid.gui.components.inventory;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.cells.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.dialog.resource.ResourceDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Waverunner on 3/20/2017
 */
public class InventoryControl extends VBox {
    @FXML
    ListView<GalaxyResource> inventoryListView;
    @FXML
    Button removeButton;
    private List<GalaxyResource> inventoryList;

    public InventoryControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inventory_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setup();
    }

    private void setup() {
        inventoryListView.setCellFactory(param -> new GalaxyResourceListCell());
        removeButton.disableProperty().bind(Bindings.isEmpty(inventoryListView.getSelectionModel().getSelectedItems()));
    }

    @FXML
    protected void removeSelectedResource() {
        GalaxyResource selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !inventoryList.contains(selectedItem))
            return;

        inventoryList.remove(selectedItem);
    }


    @FXML
    public void addGalaxyResource() {
        ResourceDialog dialog = new ResourceDialog();
        dialog.setTitle("Add Resource to Inventory");
        Optional<GalaxyResource> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        GalaxyResource galaxyResource = result.get();
        boolean exists = false;
        for (GalaxyResource resource : inventoryList) {
            if (resource.getName().equals(galaxyResource.getName())) {
                exists = true;
                break;
            }
        }
        if (!exists)
            inventoryList.add(galaxyResource);
    }

    public void setInventoryList(FilteredList<GalaxyResource> filteredInventoryList) {
        this.inventoryList = (List<GalaxyResource>) filteredInventoryList.getSource();
        inventoryListView.setItems(filteredInventoryList);
    }

    public BooleanProperty disableInventoryItemsProperty() {
        return inventoryListView.disableProperty();
    }

}
