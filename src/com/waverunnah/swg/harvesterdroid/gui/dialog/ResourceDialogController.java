package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.converters.ResourceValueConverter;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceDialogController implements Initializable {
	private ObjectProperty<GalaxyResource> galaxyResource = new SimpleObjectProperty<>();
	private ObservableList<String> resourceTypes = FXCollections.observableArrayList(HarvesterDroid.getCurrentResourcesXml().getTypes());
	// TODO Figure out easy way to convert data from dialog -> resourceListItem -> galaxyResource

	@FXML
	TextField nameField;
	@FXML
	ComboBox<String> typeComboBox;
	@FXML
	FlowPane attributesPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ResourceDialog.setController(this);
		typeComboBox.setItems(resourceTypes);
		galaxyResource.addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				populateFromGalaxyResource(newValue);
		});
		attributesPane.setPrefWrapLength((55 * Attributes.size()) / 2);
		editResourceItem(new GalaxyResource());
	}

	public void editResourceItem(GalaxyResource galaxyResource) {
		if (galaxyResource == null)
			galaxyResource = new GalaxyResource();
		this.galaxyResource.set(galaxyResource);
	}

	private void populateFromGalaxyResource(GalaxyResource galaxyResource) {
		attributesPane.getChildren().clear();
		nameField.textProperty().bindBidirectional(galaxyResource.nameProperty());
		galaxyResource.resourceTypeProperty().bind(typeComboBox.getSelectionModel().selectedItemProperty());
		Attributes.forEach((primary, secondary) -> bindAttribute(primary, galaxyResource.getAttributes().get(primary)));
	}

	private void bindAttribute(String attribute, IntegerProperty property) {
		HBox group = new HBox();
		group.setAlignment(Pos.CENTER);
		group.setSpacing(5);
		Label name = new Label(attribute);
		group.getChildren().add(name);
		TextField textField = new TextField();
		textField.setPrefWidth(25);
		group.getChildren().add(textField);

		textField.textProperty().bindBidirectional(property, new ResourceValueConverter());

		attributesPane.getChildren().add(group);
	}

	public GalaxyResource getResourceListItem() {
		return galaxyResource.get();
	}

	public void retrieveStats() {
		// TODO Retrieve stats from site that HarvesterDroid is configured to use
		// TODO Disable editing for value boxes once properly integrated
	}

}
