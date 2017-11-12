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

package io.github.waverunner.harvesterdroid.app.ui.converters;

import javafx.util.StringConverter;

public class ResourceValueConverter extends StringConverter<Number> {

  @Override
  public String toString(Number object) {
    int value = object.intValue();
    return value == -1 ? "--" : String.valueOf(value);
  }

  @Override
  public Integer fromString(String string) {
    if (!"0123456789".contains(string)) {
      return -1;
    }

    int value = Integer.parseInt(string);
    if (value > 1000 || value <= 0) {
      return -1;
    }

    return value;
  }
}
