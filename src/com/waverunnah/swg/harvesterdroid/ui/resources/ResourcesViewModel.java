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

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.ui.items.GalaxyResourceItemViewModel;
import com.waverunnah.swg.harvesterdroid.ui.scopes.GalaxyScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.ResourceScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.SchematicScope;
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
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Waverunner on 4/3/2017
 */
public class ResourcesViewModel implements ViewModel {
    private ListProperty<GalaxyResourceItemViewModel> galaxyResources = new SimpleListProperty<>();
    private ObjectProperty<FilteredList<GalaxyResourceItemViewModel>> resources = new SimpleObjectProperty<>();
    private ObjectProperty<GalaxyResourceItemViewModel> selected = new SimpleObjectProperty<>();

    private Command favoriteCommand;

    @InjectScope
    private SchematicScope schematicScope;
    @InjectScope
    private GalaxyScope galaxyScope;
    @InjectScope
    private ResourceScope resourceScope;

    private final HarvesterDroid harvesterDroid;

    public ResourcesViewModel(HarvesterDroid harvesterDroid) {
        this.harvesterDroid = harvesterDroid;
    }

    public void initialize() {

        favoriteCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                resourceScope.publish(ResourceScope.FAVORITE, selected.get());
            }
        });

        galaxyResources.addListener(((observable, oldValue, newValue) -> {
            if (newValue != null)
                resources.set(new FilteredList<>(newValue, galaxyResource -> false));
        }));

        galaxyResources.set(FXCollections.observableArrayList(harvesterDroid.getResources()
                .stream().map(GalaxyResourceItemViewModel::new).collect(Collectors.toList())));

        schematicScope.subscribe(SchematicScope.ACTIVE, (s, objects) -> onSchematicSelected((Schematic) objects[0]));

        galaxyScope.subscribe(GalaxyScope.CHANGED, (s, objects) -> galaxyResources.set(FXCollections.observableArrayList(harvesterDroid.getResources()
                .stream().map(GalaxyResourceItemViewModel::new).collect(Collectors.toList()))));
    }

    private void onSchematicSelected(Schematic newValue) {
        if (newValue == null) {
            resources.get().setPredicate(param -> false);
            return;
        }

        List<GalaxyResource> bestResources = harvesterDroid.getBestResourcesList(newValue);
        resources.get().setPredicate(param -> bestResources.contains(param.getGalaxyResource()));
    }

    public ObservableList<GalaxyResourceItemViewModel> getGalaxyResources() {
        return galaxyResources.get();
    }

    public ListProperty<GalaxyResourceItemViewModel> galaxyResourcesProperty() {
        return galaxyResources;
    }

    public void setGalaxyResources(ObservableList<GalaxyResourceItemViewModel> galaxyResources) {
        this.galaxyResources.set(galaxyResources);
    }

    public FilteredList<GalaxyResourceItemViewModel> getResources() {
        return resources.get();
    }

    public ObjectProperty<FilteredList<GalaxyResourceItemViewModel>> resourcesProperty() {
        return resources;
    }

    public void setResources(FilteredList<GalaxyResourceItemViewModel> resources) {
        this.resources.set(resources);
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

    public Command getFavoriteCommand() {
        return favoriteCommand;
    }
}