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

package com.waverunnah.swg.harvesterdroid.ui.scopes;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by Waverunner on 4/3/2017
 */
public class SchematicScope implements Scope {
    private ObjectProperty<Schematic> schematic = new SimpleObjectProperty<>();

    public static String UPDATE = "SchematicScope.Update";
    public static String ACTIVE = "SchematicScope.Active";

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