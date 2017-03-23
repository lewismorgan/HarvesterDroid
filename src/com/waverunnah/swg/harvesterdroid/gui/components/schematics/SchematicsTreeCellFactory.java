package com.waverunnah.swg.harvesterdroid.gui.components.schematics;


import javafx.scene.control.TreeCell;

/**
 * Created by Waverunner on 3/23/2017
 */
public class SchematicsTreeCellFactory extends TreeCell<SchematicsTreeItem> {
    // TODO Context menus
    @Override
    protected void updateItem(SchematicsTreeItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            setText(item.getName());
        } else {
            setGraphic(null);
            setText(null);
        }
    }


}
