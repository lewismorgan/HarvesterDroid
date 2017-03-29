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

package com.waverunnah.swg.harvesterdroid.data.resources;

import java.util.Map;

/**
 * Created by Waverunner on 3/23/2017
 */
public class ResourceType {
    private String id;
    private String name;
    private String container;
    private String group;
    private boolean recylced;
    private Map<String, Integer> minMaxMap;

    public ResourceType() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public Map<String, Integer> getMinMaxMap() {
        return minMaxMap;
    }

    public boolean isRecylced() {
        return recylced;
    }

    public void setRecylced(boolean recylced) {
        this.recylced = recylced;
    }

    public void setMinMaxMap(Map<String, Integer> minMaxMap) {
        this.minMaxMap = minMaxMap;
    }
}
