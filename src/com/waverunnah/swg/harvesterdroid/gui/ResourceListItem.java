package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.converters.ResourceValueConverter;
import com.waverunnah.swg.harvesterdroid.utils.Attributes;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
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
	private HBox resourceStatsBox;
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

		galaxyResource.addListener((observable, old, val) -> handleGalaxyResourceSet(val));
	}

	public ResourceListItem(GalaxyResource item) {
		this();
		setGalaxyResource(item);
	}

	public void handleGalaxyResourceSet(GalaxyResource val) {
		if (val == null) {
			resourceName.textProperty().unbind();
			resourceType.textProperty().unbind();
			resourceImage.imageProperty().unbind();
			resourceStatsBox.getChildren().clear();
			return;
		}

		resourceName.textProperty().bindBidirectional(val.nameProperty());
		resourceType.textProperty().bindBidirectional(val.resourceTypeProperty());

		resourceImage.setImage(getImage(val.containerProperty().get()));
		val.containerProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				resourceImage.setImage(getImage(newValue));
		});

		// Ensures no duplicates are made
		resourceStatsBox.getChildren().clear();

		Attributes.forEach((primary, secondary) -> {
			IntegerProperty value = val.getAttributes().get(primary);
			createAttributeUI(secondary, value);
		});
	}

	private void createAttributeUI(String simple, IntegerProperty valueProperty) {
		VBox group = new VBox();
		group.setAlignment(Pos.CENTER);
		group.setPadding(new Insets(5.0, 0, 0, 0));
		group.disableProperty().bind(valueProperty.isEqualTo(-1));

		Label nameLabel = new Label(simple);
		nameLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(nameLabel);

		Label valueLabel = new Label("--");
		valueLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(valueLabel);

		Bindings.bindBidirectional(valueLabel.textProperty(), valueProperty, new ResourceValueConverter());

		resourceStatsBox.getChildren().add(group);
	}

	private Image getImage(String container) {
		if (container == null)
			return null;
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

	public SimpleObjectProperty<GalaxyResource> galaxyResourceProperty() {
		return galaxyResource;
	}
}
