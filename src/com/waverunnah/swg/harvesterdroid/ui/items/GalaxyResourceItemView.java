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

package com.waverunnah.swg.harvesterdroid.ui.items;

import com.waverunnah.swg.harvesterdroid.app.Attributes;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.gui.converters.ResourceValueConverter;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 4/3/2017
 */
@SuppressWarnings("Duplicates")
public class GalaxyResourceItemView implements FxmlView<GalaxyResourceItemViewModel>, Initializable {
    @FXML
    ImageView resourceImage;
    @FXML
    private Label resourceName;
    @FXML
    private Label resourceType;
    @FXML
    private HBox resourceStatsBox;

    @InjectViewModel
    private GalaxyResourceItemViewModel viewModel;

    public void initialize(URL location, ResourceBundle resources) {
        resourceImage.imageProperty().bind(viewModel.imageProperty());
        resourceName.textProperty().bind(viewModel.nameProperty());
        resourceType.textProperty().bind(viewModel.typeProperty());

        viewModel.attributesProperty().addListener((MapChangeListener<String, IntegerProperty>) change -> {
            refreshAttributesUI();
        });

        refreshAttributesUI();
    }

    private void refreshAttributesUI() {
        resourceStatsBox.getChildren().clear();
        Attributes.forEach((primary, secondary) -> {
            IntegerProperty value = viewModel.getAttributes().get(primary);
            Label percentage = createPercentageLabel(primary, viewModel.getGalaxyResource());
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
}