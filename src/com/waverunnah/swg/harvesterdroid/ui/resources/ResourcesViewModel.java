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
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

    private ReadOnlyStringWrapper statusText = new ReadOnlyStringWrapper();

    private BooleanProperty schematicSelected = new SimpleBooleanProperty(false);

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

        galaxyResources.addListener((ListChangeListener<GalaxyResourceItemViewModel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<GalaxyResource> toAdd = c.getAddedSubList().stream().map(GalaxyResourceItemViewModel::getGalaxyResource).collect(Collectors.toList());
                    toAdd.forEach(resource -> {
                        if (!harvesterDroid.getResources().contains(resource))
                            harvesterDroid.getResources().add(resource);
                    });
                } else if (c.wasRemoved())
                    harvesterDroid.getResources().removeAll(c.getRemoved().stream().map(GalaxyResourceItemViewModel::getGalaxyResource).collect(Collectors.toList()));
            }
        });

        schematicScope.subscribe(SchematicScope.ACTIVE, (s, objects) -> onSchematicSelected((Schematic) objects[0]));

        galaxyScope.subscribe(GalaxyScope.CHANGED, (s, objects) -> {
            galaxyResources.set(FXCollections.observableArrayList(harvesterDroid.getResources()
                .stream().map(GalaxyResourceItemViewModel::new).collect(Collectors.toList())));

            schematicScope.publish(SchematicScope.REFRESH);
        });

        resourceScope.subscribe(ResourceScope.IMPORT_ADDED, (s, objects) -> {
            for (Object object : objects) {
                boolean exists = false;
                for (GalaxyResourceItemViewModel galaxyResource : galaxyResources.get()) {
                    if (galaxyResource.getGalaxyResource() == object) {
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    galaxyResources.add(new GalaxyResourceItemViewModel((GalaxyResource) object));
            }
            resourceScope.publish(ResourceScope.UPDATED_LIST);
        });

        resourceScope.subscribe(ResourceScope.IMPORT_REMOVED, (s, objects) -> {
            for (Object object : objects) {
                GalaxyResource galaxyResource = (GalaxyResource) object;
                if (galaxyResource.getDespawnDate() == null || galaxyResource.getDespawnDate().isEmpty())
                    continue;

                GalaxyResourceItemViewModel toRemove = null;
                for (GalaxyResourceItemViewModel resource : galaxyResources) {
                    if (resource.getGalaxyResource().equals(galaxyResource)) {
                        toRemove = resource;
                        break;
                    }
                }
                galaxyResources.remove(toRemove);
                resourceScope.publish(ResourceScope.UPDATED_LIST);
            }
        });

        statusText.bind(Bindings.when(galaxyResources.emptyProperty()).then("No resources available for this galaxy, try adding one to your inventory")
                .otherwise(Bindings.when(schematicSelected.not()).then("Select a schematic to view the best available resources")
                        .otherwise(Bindings.when(Bindings.isEmpty(resources.get())).then("No resources available for this schematic")
                                .otherwise(""))));
    }

    private void onSchematicSelected(Schematic newValue) {
        if (newValue == null) {
            resources.get().setPredicate(param -> false);
            schematicSelected.set(false);
            return;
        }

        List<GalaxyResource> bestResources = harvesterDroid.getBestResourcesList(newValue);
        resources.get().setPredicate(param -> bestResources.contains(param.getGalaxyResource()));
        schematicSelected.set(true);
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

    public String getStatusText() {
        return statusText.get();
    }

    public ReadOnlyStringProperty statusTextProperty() {
        return statusText.getReadOnlyProperty();
    }
}