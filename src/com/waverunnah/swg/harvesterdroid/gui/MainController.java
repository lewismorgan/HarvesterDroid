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
import javafx.scene.input.MouseButton;
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
		bestResourcesPane.textProperty().bind(Bindings.concat("Best Resources as of ").concat(app.currentResourceTimestampProperty()));
		bestResourcesListView.disableProperty().bind(Bindings.isEmpty(app.getFilteredResources()));
		bestResourcesListView.setCellFactory(param -> new GalaxyResourceListCell());
		bestResourcesListView.setItems(app.getFilteredResources());
		bestResourcesListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    if (event.getButton() == MouseButton.PRIMARY) {
		        if (event.getClickCount() >= 2) {
                    GalaxyResource selectedItem = bestResourcesListView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && !app.getInventory().contains(selectedItem))
                        app.getInventory().add(selectedItem);
                }
            }
        });
	}

	private void initSchematics() {
		schematicsControl.focusedSchematicProperty().bindBidirectional(app.activeSchematicProperty());
		schematicsControl.itemsProperty().bind(app.schematicsProperty());
		schematicsControl.disableSchematicsViewProperty().bind(app.schematicsProperty().emptyProperty());
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
