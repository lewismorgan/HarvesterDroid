package com.waverunnah.swg.harvesterdroid.gui.callbacks;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.ResourceListItem;
import javafx.scene.control.ListCell;


public class GalaxyResourceListCell extends ListCell<GalaxyResource> {

	@Override
	protected void updateItem(GalaxyResource item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			setGraphic(new ResourceListItem(item));
		} else {
			setGraphic(null);
		}
	}
}
