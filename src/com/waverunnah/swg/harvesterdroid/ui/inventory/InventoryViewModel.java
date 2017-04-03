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

package com.waverunnah.swg.harvesterdroid.ui.inventory;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.InventoryResource;
import com.waverunnah.swg.harvesterdroid.ui.dialog.resource.ResourceDialog;
import com.waverunnah.swg.harvesterdroid.ui.items.GalaxyResourceItemViewModel;
import com.waverunnah.swg.harvesterdroid.ui.scopes.GalaxyScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.ResourceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * Created by Waverunner on 4/3/2017
 */
public class InventoryViewModel implements ViewModel {
    private ListProperty<GalaxyResourceItemViewModel> inventory = new SimpleListProperty<>();
    private ObjectProperty<GalaxyResourceItemViewModel> selected = new SimpleObjectProperty<>();

    private Command addCommand;
    private Command removeCommand;

    private final HarvesterDroid harvesterDroid;

    @InjectScope
    private ResourceScope resourceScope;
    @InjectScope
    private GalaxyScope galaxyScope;

    public InventoryViewModel(HarvesterDroid harvesterDroid) {
        this.harvesterDroid = harvesterDroid;
    }

    public void initialize() {
        initializeCommands();

        inventory.set(FXCollections.observableArrayList());

        refreshInventoryView();

        resourceScope.subscribe(ResourceScope.FAVORITE, (s, objects) -> {
            for (Object object : objects) {
                createGalaxyResourceItem(((GalaxyResourceItemViewModel) object).getGalaxyResource());
            }
        });

        galaxyScope.subscribe(GalaxyScope.CHANGED, (s, objects) -> refreshInventoryView());

        inventory.addListener((ListChangeListener<GalaxyResourceItemViewModel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> harvesterDroid.addInventoryResource(item.getGalaxyResource()));
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(item -> harvesterDroid.removeInventoryResource(item.getGalaxyResource()));
                }
            }
        });
    }

    private void refreshInventoryView() {
        inventory.clear();
        for (InventoryResource inventoryResource : harvesterDroid.getInventory()) {
            GalaxyResource galaxyResource = harvesterDroid.getGalaxyResource(inventoryResource);
            if (galaxyResource != null)
                inventory.add(new GalaxyResourceItemViewModel(galaxyResource));
        }
    }

    private void initializeCommands() {
        addCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                ResourceDialog dialog = new ResourceDialog();
                Optional<GalaxyResource> result = dialog.showAndWait();
                if (!result.isPresent())
                    return;

                GalaxyResource galaxyResource = result.get();
                createGalaxyResourceItem(galaxyResource);
            }
        });

        removeCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                inventory.remove(selected.get());
            }
        }, selected.isNotNull());
    }

    private void createGalaxyResourceItem(GalaxyResource galaxyResource) {
        boolean exists = false;
        for (GalaxyResourceItemViewModel inventoryResource : inventory) {
            GalaxyResource resource = inventoryResource.getGalaxyResource();
            if (resource.getName().equals(galaxyResource.getName())) {
                exists = true;
                break;
            }
        }

        if (!exists)
            inventory.add(new GalaxyResourceItemViewModel(galaxyResource));
    }


    public ObservableList<GalaxyResourceItemViewModel> getInventory() {
        return inventory.get();
    }

    public ListProperty<GalaxyResourceItemViewModel> inventoryProperty() {
        return inventory;
    }

    public void setInventory(ObservableList<GalaxyResourceItemViewModel> inventory) {
        this.inventory.set(inventory);
    }

    public GalaxyResourceItemViewModel getSelected() {
        return selected.get();
    }

    public ObjectProperty<GalaxyResourceItemViewModel> selectedProperty() {
        return selected;
    }

    public void setSelected(GalaxyResourceItemViewModel selected) {
        this.selected.set(selected);
    }

    public Command getAddCommand() {
        return addCommand;
    }

    public Command getRemoveCommand() {
        return removeCommand;
    }
}