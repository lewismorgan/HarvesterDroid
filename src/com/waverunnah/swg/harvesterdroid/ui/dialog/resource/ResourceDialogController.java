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

package com.waverunnah.swg.harvesterdroid.ui.dialog.resource;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.ui.converters.ResourceValueConverter;
import com.waverunnah.swg.harvesterdroid.app.Attributes;
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
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class ResourceDialogController implements Initializable {
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
    private ObjectProperty<GalaxyResource> galaxyResource = new SimpleObjectProperty<>();

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
        resourceTypeField.textProperty().bindBidirectional(galaxyResource.resourceTypeProperty(), new StringConverter<ResourceType>() {
            @Override
            public String toString(ResourceType object) {
                return object.getName();
            }

            @Override
            public ResourceType fromString(String string) {
                return null;
            }
        });
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
        GalaxyResource galaxyResource = Launcher.getApp().retrieveGalaxyResource(nameField.getText());
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
