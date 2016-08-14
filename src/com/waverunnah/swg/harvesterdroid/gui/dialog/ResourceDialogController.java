package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.converters.ResourceValueConverter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceDialogController implements Initializable {
	private ObjectProperty<GalaxyResource> galaxyResource = new SimpleObjectProperty<>();

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
		typeComboBox.setItems(FXCollections.observableArrayList(HarvesterDroid.getResourceTypes()));
		galaxyResource.addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				populateFromGalaxyResource(newValue);
		});
		editResourceItem(new GalaxyResource());
	}

	public void editResourceItem(GalaxyResource galaxyResource) {
		if (galaxyResource == null)
			galaxyResource = new GalaxyResource();
		this.galaxyResource.set(galaxyResource);
	}

	private void populateFromGalaxyResource(GalaxyResource galaxyResource) {
		nameField.textProperty().bindBidirectional(galaxyResource.nameProperty());
		galaxyResource.resourceTypeProperty().bind(typeComboBox.getSelectionModel().selectedItemProperty());
		HarvesterDroid.modifiers.forEach(modifier -> bindAttribute(modifier, galaxyResource.getAttributes().get(modifier)));
	}

	private void bindAttribute(String attribute, IntegerProperty property) {
		switch(attribute) {
			case "entangle_resistance":
				erValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "cold_resistance":
				crValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "conductivity":
				cdValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "decay_resistance":
				drValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "flavor":
				flValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "heat_resistance":
				hrValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "malleability":
				maValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "potential_energy":
				peValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "overall_quality":
				oqValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "shock_resistance":
				srValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
			case "unit_toughness":
				utValue.textProperty().bindBidirectional(property, new ResourceValueConverter());
				break;
		}
	}

	public GalaxyResource getResourceListItem() {
		return galaxyResource.get();
	}

	public void retrieveStats() {
		// TODO Retrieve stats from site that HarvesterDroid is configured to use
		// TODO Disable editing for value boxes once properly integrated
	}

}
