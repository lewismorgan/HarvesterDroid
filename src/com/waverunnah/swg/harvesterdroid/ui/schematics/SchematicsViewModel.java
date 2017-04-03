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

package com.waverunnah.swg.harvesterdroid.ui.schematics;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.schematic.SchematicDialog;
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

import java.util.Optional;

/**
 * Created by Waverunner on 4/3/2017
 */
@SuppressWarnings("Duplicates")
public class SchematicsViewModel implements ViewModel {
    private ListProperty<Schematic> schematics = new SimpleListProperty<>();
    private ObjectProperty<Schematic> selected = new SimpleObjectProperty<>();

    private Command removeCommand;
    private Command addCommand;
    private Command editCommand;

    @InjectScope
    private SchematicScope schematicScope;

    private final HarvesterDroid harvesterDroid;

    public SchematicsViewModel(HarvesterDroid harvesterDroid){
        this.harvesterDroid = harvesterDroid;
        init();
    }

    private void init() {
        schematics.set(FXCollections.observableArrayList(harvesterDroid.getSchematics()));

        removeCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                schematics.remove(selected.get());
            }
        }, selected.isNotNull());

        editCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                handleEditSelectedSchematic();
            }
        }, selected.isNotNull());

        addCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                displaySchematicDialog();
            }
        });

        selected.addListener(((observable, oldValue, newValue) -> handleSchematicSelected(newValue)));
    }

    private void handleSchematicSelected(Schematic schematic) {
        schematicScope.setSchematic(schematic);
        schematicScope.publish(SchematicScope.ACTIVE);
    }

    private void handleEditSelectedSchematic() {
        Schematic selection = selected.get();
        if (selection == null)
            displaySchematicDialog();
        else
            displaySchematicDialog(selection);
    }

    private void displaySchematicDialog() {
        SchematicDialog dialog = new SchematicDialog();
        dialog.setTitle("Create Schematic");
        Optional<Schematic> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        Schematic schematic = result.get();
        schematics.add(schematic);
    }

    private void displaySchematicDialog(Schematic schematic) {
        SchematicDialog dialog = new SchematicDialog(schematic);
        dialog.setTitle("Edit Schematic");
        Optional<Schematic> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        schematics.remove(schematic);
        schematics.add(result.get());
    }

    public ObservableList<Schematic> getSchematics() {
        return schematics.get();
    }

    public ListProperty<Schematic> schematicsProperty() {
        return schematics;
    }

    public void setSchematics(ObservableList<Schematic> schematics) {
        this.schematics.set(schematics);
    }

    public Schematic getSelected() {
        return selected.get();
    }

    public ObjectProperty<Schematic> selectedProperty() {
        return selected;
    }

    public void setSelected(Schematic selected) {
        this.selected.set(selected);
    }

    public Command getRemoveCommand() {
        return removeCommand;
    }

    public Command getAddCommand() {
        return addCommand;
    }

    public Command getEditCommand() {
        return editCommand;
    }
}