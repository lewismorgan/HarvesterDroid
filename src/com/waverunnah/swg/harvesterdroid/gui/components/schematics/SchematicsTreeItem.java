package com.waverunnah.swg.harvesterdroid.gui.components.schematics;

/**
 * Created by Waverunner on 3/23/2017
 */
public class SchematicsTreeItem {
    private String name;
    private String identifier;
    private boolean group;

    public SchematicsTreeItem(String name, String identifier, boolean isGroup) {
        this.name = name;
        this.identifier = identifier;
        this.group = isGroup;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isGroup() {
        return group;
    }
}
