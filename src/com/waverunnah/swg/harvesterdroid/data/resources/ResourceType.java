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
