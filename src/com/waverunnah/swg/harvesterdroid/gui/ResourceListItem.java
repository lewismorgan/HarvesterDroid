/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
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
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;

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

		resourceName.textProperty().bind(val.nameProperty());
		resourceType.textProperty().bindBidirectional(val.resourceTypeProperty(), new StringConverter<ResourceType>() {
			@Override
			public String toString(ResourceType object) {
				return object.getName();
			}

			@Override
			public ResourceType fromString(String string) {
				return null;
			}
		});

		resourceImage.setImage(getImage(val.containerProperty().get()));
		val.containerProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				resourceImage.setImage(getImage(newValue));
		});

		// Ensures no duplicates are made
		resourceStatsBox.getChildren().clear();

		Attributes.forEach((primary, secondary) -> {
			IntegerProperty value = val.getAttributes().get(primary);
			Label percentage = createPercentageLabel(primary, val);
			createAttributeUI(secondary, value, percentage);
		});
	}

	private void createAttributeUI(String simple, IntegerProperty valueProperty, Label percentageLabel) {
		VBox group = new VBox();
		group.setAlignment(Pos.CENTER);
		group.setPadding(new Insets(5.0, 5, 5, 5));
		group.disableProperty().bind(valueProperty.isEqualTo(-1));

		Label nameLabel = new Label(simple);
		nameLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(nameLabel);

		Label valueLabel = new Label("--");
		valueLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(valueLabel);

		percentageLabel.setContentDisplay(ContentDisplay.CENTER);
		group.getChildren().add(percentageLabel);

		Bindings.bindBidirectional(valueLabel.textProperty(), valueProperty, new ResourceValueConverter());

		resourceStatsBox.getChildren().add(group);
	}

	private Image getImage(String container) {
		if (container == null)
			return null;
		InputStream is = getClass().getResourceAsStream("/images/resources/" + container + ".png");
		if (is == null) {
			container = container.split("_")[0];
			is = getClass().getResourceAsStream("/images/resources/" + container + ".png");
			if (is == null) {
				System.out.println("Could not find image /images/resources/" + container + ".png");
				return null;
			}
		}
		return new Image(is);
	}

    private Label createPercentageLabel(String attribute, GalaxyResource galaxyResource) {
        attribute = Attributes.getAbbreviation(attribute);

        ResourceType type = galaxyResource.getResourceType();
        if (!galaxyResource.getResourceType().getMinMaxMap().containsKey(attribute + "max")) {
            return new Label("--");
        }

        float max = type.getMinMaxMap().get(attribute + "max");
        if (max <= 0)
            return new Label("--");

        float min = type.getMinMaxMap().get(attribute + "min");

        float value = galaxyResource.getAttribute(Attributes.getFullName(attribute));
        float result = (value - min) / (max - min);

        if (Float.isNaN(result)) // At the max cap
            result = 1;

        Label label = new Label();
        label.setText("(" + String.valueOf(Math.round(result * 100)) + "%" + ")");
        if (result == 1)
            label.setTextFill(Color.RED);
        else if (result >= .8)
            label.setTextFill(Color.DARKORANGE);
        else if (result >= .7)
            label.setTextFill(Color.ORANGE);
        return label;
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
