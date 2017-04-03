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
import com.waverunnah.swg.harvesterdroid.gui.dialog.preferences.PreferencesDialog;
import com.waverunnah.swg.harvesterdroid.ui.scopes.GalaxyScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.ResourceScope;
import com.waverunnah.swg.harvesterdroid.ui.scopes.SchematicScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.collections.FXCollections;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Waverunner on 4/3/2017
 */
@SuppressWarnings("Duplicates")
@Singleton
@ScopeProvider(scopes = {SchematicScope.class, GalaxyScope.class, ResourceScope.class})
public class MainViewModel implements ViewModel {

    private final HarvesterDroid harvesterDroid;

    private Command preferencesCommand;

    @InjectScope
    private GalaxyScope galaxyScope;

    public MainViewModel(HarvesterDroid harvesterDroid) {
        this.harvesterDroid = harvesterDroid;
    }

    public void initialize() {
        preferencesCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                PreferencesDialog dialog = new PreferencesDialog();
                dialog.setGalaxies(FXCollections.observableMap(harvesterDroid.getGalaxies()));
                dialog.setProperties(DroidProperties.getProperties());
                Optional<Properties> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Properties properties = result.get();
                    harvesterDroid.switchToGalaxy(properties.getProperty(DroidProperties.GALAXY));
                    DroidProperties.setProperties(properties);

                    galaxyScope.publish(GalaxyScope.CHANGED);
                }
            }
        });
    }

    public Command getPreferencesCommand() {
        return preferencesCommand;
    }
}