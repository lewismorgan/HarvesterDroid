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

package com.lewisjmorgan.harvesterdroid.app.ui.scopes;

import com.lewisjmorgan.harvesterdroid.app.data.schematics.Schematic;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class SchematicScope implements Scope {

  public static final String UPDATE = "SchematicScope.Update";
  public static final String ACTIVE = "SchematicScope.Active";
  public static final String REFRESH = "SchematicScope.Refresh";
  public static final String IMPORT = "SchematicScope.Import";
  private ObjectProperty<Schematic> schematic = new SimpleObjectProperty<>();

  public Schematic getSchematic() {
    return schematic.get();
  }

  public void setSchematic(Schematic schematic) {
    this.schematic.set(schematic);
  }

  public ObjectProperty<Schematic> schematicProperty() {
    return schematic;
  }
}
