package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.ResourceListItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceDialogController implements Initializable {
	private ResourceListItem resourceListItem;

	// TODO Figure out easy way to convert data from dialog -> resourceListItem -> galaxyResource

	@FXML
	TextField nameField;
	@FXML
	ComboBox<String> typeComboBox;
	@FXML
	TextField erValue;
	@FXML
	TextField crValue;
	@FXML
	TextField cdValue;
	@FXML
	TextField drValue;
	@FXML
	TextField flValue;
	@FXML
	TextField hrValue;
	@FXML
	TextField maValue;
	@FXML
	TextField peValue;
	@FXML
	TextField oqValue;
	@FXML
	TextField srValue;
	@FXML
	TextField utValue;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ResourceDialog.setController(this);
		editResourceItem(new ResourceListItem());
	}

	public void editResourceItem(ResourceListItem resourceListItem) {
		if (resourceListItem == null) {
			resourceListItem = new ResourceListItem();
		}

		this.resourceListItem = resourceListItem;

		if (resourceListItem.getGalaxyResource() != null)
			populateFromGalaxyResource(resourceListItem.getGalaxyResource());
		else
			createNewGalaxyResource();
	}

	private void createNewGalaxyResource() {
		GalaxyResource galaxyResource = new GalaxyResource();
		galaxyResource.setName("testing");
		galaxyResource.setResourceType("copper_polysteel");
		galaxyResource.setContainer("copper");
		resourceListItem.setGalaxyResource(galaxyResource);
		System.out.println("New resource created");
	}

	private void populateFromGalaxyResource(GalaxyResource galaxyResource) {

	}

	public ResourceListItem getResourceListItem() {
		return resourceListItem;
	}

	public void retrieveStats() {
		// TODO Retrieve stats from site that HarvesterDroid is configured to use
		// TODO Disable editing for value boxes once properly integrated
	}

}
