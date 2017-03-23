package com.waverunnah.swg.harvesterdroid.data.resources;

import java.util.Map;

/**
 * Created by Waverunner on 3/23/2017
 */
public class ResourceType {
    private String name;
    private String fullName;
    private String category;
    private String group;
    private String container;
    private String inventoryType;
    private Map<String, Integer> minMaxMap;

    public ResourceType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public void setMinMaxMap(Map<String, Integer> minMaxMap) {
        this.minMaxMap = minMaxMap;
    }
}
