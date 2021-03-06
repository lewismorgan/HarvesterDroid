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

package com.lewisjmorgan.harvesterdroid.app.data.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Waverunner on 3/31/2017.
 */
public class InventoryResource {

  private String name;
  private String tracker;
  private String galaxy;

  @JsonCreator
  public InventoryResource(@JsonProperty("name") String name, @JsonProperty("tracker") String tracker,
      @JsonProperty("galaxy") String galaxy) {
    this.name = name;
    this.tracker = tracker;
    this.galaxy = galaxy;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTracker() {
    return tracker;
  }

  public void setTracker(String tracker) {
    this.tracker = tracker;
  }

  public String getGalaxy() {
    return galaxy;
  }

  public void setGalaxy(String galaxy) {
    this.galaxy = galaxy;
  }
}
