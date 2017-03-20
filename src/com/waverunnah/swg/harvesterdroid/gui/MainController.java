package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.callbacks.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.components.inventory.InventoryControl;
import com.waverunnah.swg.harvesterdroid.gui.components.schematics.SchematicsControl;
import com.waverunnah.swg.harvesterdroid.gui.dialog.AboutDialog;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.stage.WindowEvent;
import org.controlsfx.control.StatusBar;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	private HarvesterDroid app;

	@FXML
	InventoryControl inventoryControl;
	@FXML
	SchematicsControl schematicsControl;
	@FXML
	TitledPane bestResourcesPane;
	@FXML
	ListView<GalaxyResource> bestResourcesListView;
	@FXML
	StatusBar statusBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		app = Launcher.getApp();
		initResources();
		initInventory();
		initSchematics();
	}

	private void initInventory() {
		inventoryControl.setInventoryList(app.getFilteredInventory());
		inventoryControl.disableInventoryItemsProperty().bind(app.inventoryProperty().emptyProperty());
	}

	private void initResources() {
		bestResourcesPane.setText("Best Resources as of " + Launcher.getApp().getLastUpdate() + " according to your inventory and the current resource spawns");
		bestResourcesListView.disableProperty().bind(Bindings.isEmpty(app.getFilteredResources()));
		bestResourcesListView.setCellFactory(param -> new GalaxyResourceListCell());
		bestResourcesListView.setItems(app.getFilteredResources());
	}

	private void initSchematics() {
		app.activeSchematicProperty().bind(schematicsControl.activeSchematicProperty());
		app.activeGroupProperty().bind(schematicsControl.activeGroupProperty());
		schematicsControl.groupsProperty().bind(app.groupsProperty());
		schematicsControl.setItems(app.getFilteredSchematics());
		schematicsControl.disableListViewProperty().bind(app.schematicsProperty().emptyProperty());
		app.displayingAllGroupsProperty().bind(schematicsControl.disableGroupsProperty());
	}

	public void save() {
		try {
			app.save();
		} catch (IOException | TransformerException e) {
			new ExceptionDialog(e).show();
		}
	}

	public void close() {
		Launcher.getStage().fireEvent(new WindowEvent(Launcher.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	public void about() {
		new AboutDialog().show();
	}
}
