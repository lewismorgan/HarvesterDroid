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

package com.waverunnah.swg.harvesterdroid.ui.main;

import com.waverunnah.swg.harvesterdroid.DroidProperties;
import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.ui.dialog.about.AboutDialog;
import com.waverunnah.swg.harvesterdroid.ui.dialog.preferences.PreferencesDialog;
import com.waverunnah.swg.harvesterdroid.ui.dialog.resource.ImportResourcesDialog;
import com.waverunnah.swg.harvesterdroid.ui.scopes.GalaxyScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.ResourceScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.SchematicScope;
import com.waverunnah.swg.harvesterdroid.xml.XmlFactory;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.waverunnah.swg.harvesterdroid.app.HarvesterDroidData.XML_INVENTORY;
import static com.waverunnah.swg.harvesterdroid.app.HarvesterDroidData.XML_SCHEMATICS;

/**
 * Created by Waverunner on 4/3/2017
 */
@Singleton
@ScopeProvider(scopes = {SchematicScope.class, GalaxyScope.class, ResourceScope.class})
public class MainViewModel implements ViewModel {

    private final HarvesterDroid harvesterDroid;

    private ReadOnlyStringWrapper statusText = new ReadOnlyStringWrapper();

    private Command preferencesCommand;
    private Command saveCommand;
    private Command aboutCommand;
    private Command importResourcesCommand;

    @InjectScope
    private GalaxyScope galaxyScope;
    @InjectScope
    private ResourceScope resourceScope;
    @InjectScope
    private SchematicScope schematicScope;

    private StringProperty galaxyString = new SimpleStringProperty();
    private StringProperty resourcesString = new SimpleStringProperty();

    public MainViewModel(HarvesterDroid harvesterDroid) {
        this.harvesterDroid = harvesterDroid;
    }

    public void initialize() {
        createCommands();

        galaxyScope.subscribe(GalaxyScope.CHANGED, (s, objects) -> updateStatusText(harvesterDroid.getActiveGalaxy(), harvesterDroid.getResources().size()));
        resourceScope.subscribe(ResourceScope.UPDATED_LIST, (s, objects) -> updateResourceStatus(harvesterDroid.getResources().size()));

        statusText.bind(Bindings.concat("Galaxy: ", galaxyString, "  |  ", "Loaded Resources: ", resourcesString));
        updateStatusText(harvesterDroid.getActiveGalaxy(), harvesterDroid.getResources().size());
    }

    private void createCommands() {
        preferencesCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                PreferencesDialog dialog = new PreferencesDialog();
                dialog.setGalaxies(FXCollections.observableMap(harvesterDroid.getGalaxies()));
                dialog.setProperties(DroidProperties.getProperties());
                Optional<Properties> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Properties properties = result.get();
                    DroidProperties.setProperties(properties);
                    if (harvesterDroid.switchToGalaxy(properties.getProperty(DroidProperties.GALAXY)))
                        galaxyScope.publish(GalaxyScope.CHANGED);
                }
            }
        });

        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {

                harvesterDroid.saveSchematics(new FileOutputStream(new File(XML_SCHEMATICS)));
                harvesterDroid.saveInventory(new FileOutputStream(new File(XML_INVENTORY)));
                publish("StatusUpdate", "Saving Resources");
                harvesterDroid.saveResources();
                publish("StatusUpdate.Finished");
            }
        });

        aboutCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                new AboutDialog().show();
            }
        });

        importResourcesCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                ImportResourcesDialog dialog = new ImportResourcesDialog();
                Optional<List<String>> resourceNames = dialog.showAndWait();
                resourceNames.ifPresent(strings -> {
                    GalaxyResource[] toImport = new GalaxyResource[strings.size()];
                    for (int i = 0; i < strings.size(); i++) {
                        toImport[i] = harvesterDroid.retrieveGalaxyResource(strings.get(i));
                    }

                    List<GalaxyResource> imported = new ArrayList<>();
                    for (GalaxyResource galaxyResource : toImport) {
                        if (galaxyResource != null)
                            imported.add(galaxyResource);
                    }

                    resourceScope.publish(ResourceScope.IMPORT_ADDED, imported.toArray());
                });
            }
        });
    }

    private void updateStatusText(String galaxy, int resources) {
        updateGalaxyStatus(galaxy);
        updateResourceStatus(resources);
    }

    public void importSchematics(File... schematicsFiles) {
        for (File file : schematicsFiles) {
            try {

                SchematicsXml schematicsXml = XmlFactory.read(SchematicsXml.class, new FileInputStream(file));
                if (schematicsXml != null && schematicsXml.getSchematics() != null)
                    schematicScope.publish(SchematicScope.IMPORT, schematicsXml.getSchematics().toArray());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateGalaxyStatus(String galaxy) {
        galaxyString.set(galaxy);
    }

    private void updateResourceStatus(int resources) {
        resourcesString.set(String.valueOf(resources));
    }

    public Command getPreferencesCommand() {
        return preferencesCommand;
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

    public Command getAboutCommand() {
        return aboutCommand;
    }

    public Command getImportResourcesCommand() {
        return importResourcesCommand;
    }

    public String getStatusText() {
        return statusText.get();
    }

    public void setStatusText(String statusText) {
        this.statusText.set(statusText);
    }

    public ReadOnlyStringProperty statusTextProperty() {
        return statusText.getReadOnlyProperty();
    }
}