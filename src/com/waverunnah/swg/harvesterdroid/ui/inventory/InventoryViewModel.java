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
import com.waverunnah.swg.harvesterdroid.ui.items.GalaxyResourceItemViewModel;
import com.waverunnah.swg.harvesterdroid.ui.scopes.ResourceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Waverunner on 4/3/2017
 */
public class InventoryViewModel implements ViewModel {
    private ListProperty<GalaxyResourceItemViewModel> inventory = new SimpleListProperty<>();

    private final HarvesterDroid harvesterDroid;

    @InjectScope
    private ResourceScope resourceScope;

    public InventoryViewModel(HarvesterDroid harvesterDroid) {
        this.harvesterDroid = harvesterDroid;
    }

    public void initialize() {
        inventory.set(FXCollections.observableArrayList());

        for (InventoryResource inventoryResource : harvesterDroid.getInventory()) {
            GalaxyResource galaxyResource = harvesterDroid.getGalaxyResource(inventoryResource);
            if (galaxyResource != null)
                inventory.add(new GalaxyResourceItemViewModel(galaxyResource));
        }

        resourceScope.subscribe(ResourceScope.FAVORITE, (s, objects) -> {
            GalaxyResource galaxyResource = (GalaxyResource) objects[0];

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
        });
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
}