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

package com.lewisjmorgan.harvesterdroid.app.ui.dialog.resource;

import com.lewisjmorgan.harvesterdroid.api.GalaxyResource;
import com.lewisjmorgan.harvesterdroid.api.resource.Attributes;
import com.lewisjmorgan.harvesterdroid.app.Launcher;
import java.net.URL;
import java.util.ResourceBundle;
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
      if (newValue != null) {
        populateFromGalaxyResource(newValue);
      }
    });
  }

  private void populateFromGalaxyResource(GalaxyResource galaxyResource) {
    attributesGroup.getChildren().clear();
    resourceTypeField.setText(galaxyResource.getResourceType().getName());
    Attributes.forEach((primary, secondary) -> bindAttribute(primary, galaxyResource.getAttributes().get(primary)));

    if (galaxyResource.getDespawnDate() != null) {
      infoLeftLabel.setText("Despawned on");
      infoRightLabel.setText(galaxyResource.getDespawnDate().toString());
    } else {
      infoLeftLabel.setText("Available since");
      infoRightLabel.setText(galaxyResource.getSpawnDate().toString());
    }
  }

  private void bindAttribute(String attribute, int value) {
    VBox group = new VBox();
    group.setAlignment(Pos.CENTER);
    group.setPadding(new Insets(5.0, 0, 0, 0));
    group.disableProperty().set(value == -1);

    Label nameLabel = new Label(Attributes.getAbbreviation(attribute));
    nameLabel.setContentDisplay(ContentDisplay.CENTER);
    group.getChildren().add(nameLabel);

    Label valueLabel = new Label(value == -1 ? "--" : String.valueOf(value));
    valueLabel.setContentDisplay(ContentDisplay.CENTER);
    group.getChildren().add(valueLabel);

    attributesGroup.getChildren().add(group);
  }

  public GalaxyResource getGalaxyResource() {
    return galaxyResource.get();
  }

  public void retrieveStats() {
    GalaxyResource galaxyResource = Launcher.getApp().findGalaxyResource(nameField.getText());
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
