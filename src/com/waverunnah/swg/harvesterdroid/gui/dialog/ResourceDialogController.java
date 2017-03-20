package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.converters.ResourceValueConverter;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceDialogController implements Initializable {
	private ObjectProperty<GalaxyResource> galaxyResource = new SimpleObjectProperty<>();

	@FXML
	TextField resourceTypeField;
	@FXML
	TextField nameField;
	@FXML
	HBox attributesGroup;
	@FXML
	Label infoRightLabel;
	@FXML
	Label infoLeftLabel;
	@FXML
	HBox infoGroup;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ResourceDialog.setController(this);
		galaxyResource.addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				populateFromGalaxyResource(newValue);
		});
	}

	private void populateFromGalaxyResource(GalaxyResource galaxyResource) {
		attributesGroup.getChildren().clear();
		resourceTypeField.textProperty().bind(galaxyResource.resourceTypeProperty());
		Attributes.forEach((primary, secondary) -> bindAttribute(primary, galaxyResource.getAttributes().get(primary)));

		When whenUnavailable = Bindings.when(galaxyResource.despawnDateProperty().isNotEmpty());
		infoLeftLabel.textProperty().bind(whenUnavailable.then("Despawned on").otherwise("Available since"));
		infoRightLabel.textProperty().bind(whenUnavailable.then(galaxyResource.despawnDateProperty()).otherwise(galaxyResource.dateProperty()));
	}

	private void bindAttribute(String attribute, IntegerProperty property) {
		VBox group = new VBox();
		group.setAlignment(Pos.CENTER);
		group.setPadding(new Insets(5.0, 0, 0, 0));
		group.disableProperty().bind(property.isEqualTo(-1));

		Label nameLabel = new Label(Attributes.getAbbreviation(attribute));
		nameLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(nameLabel);

		Label valueLabel = new Label("--");
		valueLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(valueLabel);

		Bindings.bindBidirectional(valueLabel.textProperty(), property, new ResourceValueConverter());

		attributesGroup.getChildren().add(group);
	}

	public GalaxyResource getGalaxyResource() {
		return galaxyResource.get();
	}

	public void retrieveStats() {
		GalaxyResource galaxyResource = Launcher.getApp().getGalaxyResourceByName(nameField.getText());
		if (galaxyResource == null) {
			infoLeftLabel.setText("Couldn't find resource");
			infoRightLabel.textProperty().unbind();
			infoRightLabel.setText(nameField.getText());
			nameField.setText(null);
			return;
		}

		this.galaxyResource.set(galaxyResource);
	}
}
