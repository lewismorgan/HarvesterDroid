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

package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.DroidProperties;
import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.cells.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.components.inventory.InventoryControl;
import com.waverunnah.swg.harvesterdroid.gui.components.schematics.SchematicsControl;
import com.waverunnah.swg.harvesterdroid.gui.dialog.about.AboutDialog;
import com.waverunnah.swg.harvesterdroid.gui.dialog.preferences.PreferencesDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.stage.WindowEvent;
import org.controlsfx.control.StatusBar;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    InventoryControl inventoryControl;
    @FXML
    SchematicsControl schematicsControl;
    @FXML
    TitledPane bestResourcesPane;
    @FXML
    ListView<GalaxyResource> bestResourcesListView;
    @FXML
    StatusBar statusBar;

    private HarvesterDroid app;

    private ListProperty<Schematic> schematics = new SimpleListProperty<>();
    private ObjectProperty<Schematic> activeSchematic = new SimpleObjectProperty<>(null);

    private ObservableList<GalaxyResource> inventory;
    private FilteredList<GalaxyResource> filteredInventory;
    private ObservableList<GalaxyResource> resources;
    private FilteredList<GalaxyResource> filteredResources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        app = Launcher.getApp();
        initResources();
        initInventory();
        initSchematics();
    }

    private void initInventory() {
        inventory = FXCollections.observableArrayList(app.getInventory());
        filteredInventory = new FilteredList<>(inventory, galaxyResource -> true);
        inventoryControl.setInventoryList(filteredInventory);
        inventoryControl.disableInventoryItemsProperty().bind(Bindings.isEmpty(filteredInventory));

        inventory.addListener((ListChangeListener<? super GalaxyResource>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(galaxyResource -> {
                        if (!resources.contains(galaxyResource))
                            resources.add(galaxyResource);
                        app.addInventoryResource(galaxyResource);
                    });
                }
            }
        });
    }

    private void initResources() {
        resources = FXCollections.observableArrayList(app.getResources());
        filteredResources = new FilteredList<>(resources, galaxyResource -> false);
        bestResourcesPane.textProperty().bind(Bindings.concat("Best Resources as of ").concat(app.getCurrentResourceTimestamp()));
        bestResourcesListView.disableProperty().bind(Bindings.isEmpty(filteredResources));
        bestResourcesListView.setCellFactory(param -> new GalaxyResourceListCell());
        bestResourcesListView.setItems(filteredResources);
        bestResourcesListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() >= 2) {
                    GalaxyResource selectedItem = bestResourcesListView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && !app.getInventory().contains(selectedItem))
                        app.getInventory().add(selectedItem);
                }
            }
        });
    }

    private void initSchematics() {
        schematics.set(FXCollections.observableArrayList(app.getSchematics()));
        schematicsControl.focusedSchematicProperty().bindBidirectional(activeSchematic);
        schematicsControl.itemsProperty().bind(schematics);
        schematicsControl.disableSchematicsViewProperty().bind(schematics.emptyProperty());

        schematics.addListener((ListChangeListener<Schematic>) c -> {
            while(c.next()) {
                if (c.getAddedSize() > 0)
                    c.getAddedSubList().forEach(added -> app.getSchematics().add(added));
                if (c.getRemovedSize() > 0)
                    c.getRemoved().forEach(removed -> app.getSchematics().remove(removed));
            }
        });

        activeSchematic.addListener(this::onSchematicSelected);
    }

    private void onSchematicSelected(ObservableValue<? extends Schematic> observable, Schematic oldValue, Schematic newValue) {
        if (newValue == null) {
            filteredResources.setPredicate(param -> false);
            return;
        }

        List<GalaxyResource> bestResources = app.getBestResourcesList(newValue);
        filteredResources.setPredicate(bestResources::contains);
    }

    public void save() {
        Launcher.save();
    }

    public void close() {
        Launcher.getStage().fireEvent(new WindowEvent(Launcher.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void about() {
        new AboutDialog().show();
    }

    public void onPreferencesMenuItem() {
        PreferencesDialog dialog = new PreferencesDialog(app.getGalaxies());
        dialog.setProperties(DroidProperties.getProperties());
        Optional<Properties> result = dialog.showAndWait();
        if (result.isPresent()) {
            Properties properties = result.get();
            app.switchToGalaxy(properties.getProperty(DroidProperties.GALAXY));
            DroidProperties.setProperties(properties);
        }
    }
}
