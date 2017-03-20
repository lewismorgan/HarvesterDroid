package com.waverunnah.swg.harvesterdroid.gui.components.inventory;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.callbacks.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ResourceDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Waverunner on 3/20/2017
 */
public class InventoryControl extends VBox {
	private List<GalaxyResource> inventoryList;

	@FXML
	ListView<GalaxyResource> inventoryListView;
	@FXML
	Button removeButton;

	public InventoryControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inventory_control.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		setup();
	}

	private void setup() {
		inventoryListView.setCellFactory(param -> new GalaxyResourceListCell());
		removeButton.disableProperty().bind(Bindings.isEmpty(inventoryListView.getSelectionModel().getSelectedItems()));
	}

	@FXML
	protected void removeSelectedResource() {
		GalaxyResource selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
		if (selectedItem == null || !inventoryList.contains(selectedItem))
			return;

		inventoryList.remove(selectedItem);
	}


	@FXML
	public void addGalaxyResource() {
		ResourceDialog dialog = new ResourceDialog();
		dialog.setTitle("Add Resource to Inventory");
		Optional<GalaxyResource> result = dialog.showAndWait();
		if (!result.isPresent())
			return;

		GalaxyResource galaxyResource = result.get();
		if (!galaxyResource.getName().isEmpty() && !galaxyResource.getResourceType().isEmpty())
			inventoryList.add(galaxyResource);
	}

	public void setInventoryList(FilteredList<GalaxyResource> filteredInventoryList) {
		this.inventoryList = (List<GalaxyResource>) filteredInventoryList.getSource();
		inventoryListView.setItems(filteredInventoryList);
	}

	public BooleanProperty disableInventoryItemsProperty() {
		return inventoryListView.disableProperty();
	}

}
