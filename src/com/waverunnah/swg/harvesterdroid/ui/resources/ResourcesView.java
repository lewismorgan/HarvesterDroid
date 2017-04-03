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

package com.waverunnah.swg.harvesterdroid.ui.resources;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.cells.GalaxyResourceListCell;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 4/3/2017
 */
@SuppressWarnings("Duplicates")
public class ResourcesView implements FxmlView<ResourcesViewModel>, Initializable {

    @FXML
    private ListView<GalaxyResource> listView;

    @InjectViewModel
    private ResourcesViewModel viewModel;

    public void initialize(URL location, ResourceBundle resources) {
//        resources = FXCollections.observableArrayList(app.getGalaxyResources());
//        filteredResources = new FilteredList<>(resources, galaxyResource -> false);
//        bestResourcesPane.textProperty().bind(Bindings.concat("Best Resources as of ").concat(app.getCurrentResourceTimestamp()));

        listView.disableProperty().bind(viewModel.galaxyResourcesProperty().emptyProperty());

        listView.setCellFactory(param -> new GalaxyResourceListCell());
        listView.itemsProperty().bind(viewModel.resourcesProperty());

        listView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() >= 2) {
                    System.out.println("Double clicked :)");
/*                    GalaxyResource selectedItem = bestResourcesListView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && !app.getInventory().contains(selectedItem))
                        app.getInventory().add(selectedItem);*/
                }
            }
        });
    }
}