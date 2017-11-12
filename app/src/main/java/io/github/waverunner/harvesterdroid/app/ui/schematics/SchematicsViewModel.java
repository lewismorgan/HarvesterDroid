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

package io.github.waverunner.harvesterdroid.app.ui.schematics;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import io.github.waverunner.harvesterdroid.app.HarvesterDroid;
import io.github.waverunner.harvesterdroid.app.data.schematics.Schematic;
import io.github.waverunner.harvesterdroid.app.ui.dialog.schematic.SchematicDialog;
import io.github.waverunner.harvesterdroid.app.ui.scopes.GalaxyScope;
import io.github.waverunner.harvesterdroid.app.ui.scopes.ResourceScope;
import io.github.waverunner.harvesterdroid.app.ui.scopes.SchematicScope;
import java.util.Optional;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class SchematicsViewModel implements ViewModel {

  private final HarvesterDroid harvesterDroid;
  private ListProperty<Schematic> schematics = new SimpleListProperty<>();
  private ObjectProperty<Schematic> selected = new SimpleObjectProperty<>();
  private Command removeCommand;
  private Command addCommand;
  private Command editCommand;
  @InjectScope
  private SchematicScope schematicScope;
  @InjectScope
  private ResourceScope resourceScope;
  @InjectScope
  private GalaxyScope galaxyScope;

  public SchematicsViewModel(HarvesterDroid harvesterDroid) {
    this.harvesterDroid = harvesterDroid;
  }

  public void initialize() {
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

    schematics.addListener((ListChangeListener<Schematic>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          harvesterDroid.getSchematics().addAll(c.getAddedSubList());
        } else if (c.wasRemoved()) {
          harvesterDroid.getSchematics().removeAll(c.getRemoved());
        }
      }
    });

    resourceScope.subscribe(ResourceScope.UPDATED_LIST, (s, objects) -> reselectSchematic());
    galaxyScope.subscribe(GalaxyScope.CHANGED, (s, objects) -> reselectSchematic());
    schematicScope.subscribe(SchematicScope.REFRESH, (s, objects) -> reselectSchematic());
    schematicScope.subscribe(SchematicScope.UPDATE, (s, objects) -> publish("SchematicUpdated"));
    schematicScope.subscribe(SchematicScope.IMPORT, (s, objects) -> {
      for (Object object : objects) {
        Schematic schematic = (Schematic) object;
        if (schematic == null) {
          continue;
        }

        schematics.add(schematic);
      }
    });
  }

  private void reselectSchematic() {
    Schematic selected = getSelected();
    setSelected(null);
    setSelected(selected);

  }

  private void handleSchematicSelected(Schematic schematic) {
    schematicScope.setSchematic(schematic);
    schematicScope.publish(SchematicScope.ACTIVE, schematic);
  }

  private void handleEditSelectedSchematic() {
    Schematic selection = selected.get();
    if (selection == null) {
      displaySchematicDialog();
    } else {
      displaySchematicDialog(selection);
    }
  }

  private void displaySchematicDialog() {
    SchematicDialog dialog = new SchematicDialog();
    dialog.setTitle("Create Schematic");
    Optional<Schematic> result = dialog.showAndWait();
    if (!result.isPresent()) {
      return;
    }

    Schematic schematic = result.get();
    schematics.add(schematic);
    selected.set(schematic);
  }

  private void displaySchematicDialog(Schematic schematic) {
    SchematicDialog dialog = new SchematicDialog(schematic);
    dialog.setTitle("Edit Schematic");
    Optional<Schematic> result = dialog.showAndWait();
    if (!result.isPresent()) {
      return;
    }

    Schematic updated = result.get();
    schematicScope.setSchematic(updated);
    schematicScope.publish(SchematicScope.UPDATE, updated);
  }

  public ObservableList<Schematic> getSchematics() {
    return schematics.get();
  }

  public void setSchematics(ObservableList<Schematic> schematics) {
    this.schematics.set(schematics);
  }

  public ListProperty<Schematic> schematicsProperty() {
    return schematics;
  }

  public Schematic getSelected() {
    return selected.get();
  }

  public void setSelected(Schematic selected) {
    this.selected.set(selected);
  }

  public ObjectProperty<Schematic> selectedProperty() {
    return selected;
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