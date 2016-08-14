package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class ResourceListItem extends HBox {

	//region FXML Components
	@FXML
	ImageView resourceImage;
	@FXML
	private Label resourceName;
	@FXML
	private Label resourceType;
	@FXML
	private VBox erGroup;
	@FXML
	private VBox crGroup;
	@FXML
	private VBox cdGroup;
	@FXML
	private VBox drGroup;
	@FXML
	private VBox flGroup;
	@FXML
	private VBox hrGroup;
	@FXML
	private VBox maGroup;
	@FXML
	private VBox peGroup;
	@FXML
	private VBox oqGroup;
	@FXML
	private VBox srGroup;
	@FXML
	private VBox utGroup;
	@FXML
	private Label erValue;
	@FXML
	private Label crValue;
	@FXML
	private Label cdValue;
	@FXML
	private Label drValue;
	@FXML
	private Label flValue;
	@FXML
	private Label hrValue;
	@FXML
	private Label maValue;
	@FXML
	private Label peValue;
	@FXML
	private Label oqValue;
	@FXML
	private Label srValue;
	@FXML
	private Label utValue;
	//endregion

	private SimpleObjectProperty<GalaxyResource> galaxyResource = new SimpleObjectProperty<>();

	public ResourceListItem() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resource_list_item.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		galaxyResource.addListener((observable, old, val) -> {
			updateFromGalaxyResource(val);
		});
	}

	public void refresh() {
		updateFromGalaxyResource(galaxyResource.get());
	}

	public void updateFromGalaxyResource(GalaxyResource val) {
		// TODO Update image using group id
		if (val == null)
			return;

		resourceName.setText(val.getName());
		resourceType.setText(val.getResourceType());
		resourceImage.setImage(getImage(val.getContainer()));

		val.getAttributes().keySet().forEach(attr -> {
			int value = val.getAttribute(attr);
			switch(attr) {
				case "entangle_resistance":
					updateAttribute(erGroup, erValue, value);
					break;
				case "cold_resistance":
					updateAttribute(crGroup, crValue, value);
					break;
				case "conductivity":
					updateAttribute(cdGroup, cdValue, value);
					break;
				case "decay_resistance":
					updateAttribute(drGroup, drValue, value);
					break;
				case "flavor":
					updateAttribute(flGroup, flValue, value);
					break;
				case "heat_resistance":
					updateAttribute(hrGroup, hrValue, value);
					break;
				case "malleability":
					updateAttribute(maGroup, maValue, value);
					break;
				case "potential_energy":
					updateAttribute(peGroup, peValue, value);
					break;
				case "overall_quality":
					updateAttribute(oqGroup, oqValue, value);
					break;
				case "shock_resistance":
					updateAttribute(srGroup, srValue, value);
					break;
				case "unit_toughness":
					updateAttribute(utGroup, utValue, value);
					break;
			}
		});
	}

	private void updateAttribute(VBox attrGroup, Label attrLabel, int value) {
		attrGroup.setDisable(false);
		attrLabel.setText(String.valueOf(value));
	}

	private Image getImage(String container) {
		URL url = getClass().getResource("images/resources/" + container + ".png");
		if (url == null) {
			container = container.split("_")[0];
			url = getClass().getResource("images/resources/" + container + ".png");
			if (url == null)
				return null;
		}
		return new Image(url.toString());
	}

	public GalaxyResource getGalaxyResource() {
		return galaxyResource.get();
	}

	public void setGalaxyResource(GalaxyResource galaxyResource) {
		this.galaxyResource.set(galaxyResource);
	}

	public Label getResourceName() {
		return resourceName;
	}

	public Label getResourceType() {
		return resourceType;
	}

	public Label getErValue() {
		return erValue;
	}

	public Label getCrValue() {
		return crValue;
	}

	public Label getCdValue() {
		return cdValue;
	}

	public Label getDrValue() {
		return drValue;
	}

	public Label getFlValue() {
		return flValue;
	}

	public Label getHrValue() {
		return hrValue;
	}

	public Label getMaValue() {
		return maValue;
	}

	public Label getPeValue() {
		return peValue;
	}

	public Label getOqValue() {
		return oqValue;
	}

	public Label getSrValue() {
		return srValue;
	}

	public Label getUtValue() {
		return utValue;
	}
}
