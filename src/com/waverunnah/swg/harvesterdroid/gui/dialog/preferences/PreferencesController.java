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

package com.waverunnah.swg.harvesterdroid.gui.dialog.preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 3/31/2017
 */
public class PreferencesController implements Initializable {
    @FXML
    private ChoiceBox<String> trackerComboBox;
    @FXML
    private ChoiceBox<String> galaxyComboBox;
    @FXML
    private TextField downloadBufferTextField;
    @FXML
    private CheckBox autosaveCheckBox;
    @FXML
    private CheckBox saveNagChoiceBox;

    private ObjectProperty<Properties> properties = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        properties.addListener((observable, oldValue, newValue) -> {
            autosaveCheckBox.selectedProperty().set(Boolean.parseBoolean(newValue.getProperty("autosave")));
            saveNagChoiceBox.selectedProperty().set(Boolean.parseBoolean(newValue.getProperty("save.nag")));
        });

        trackerComboBox.setItems(FXCollections.observableArrayList("GalaxyHarvester"));
        trackerComboBox.setDisable(true);
        galaxyComboBox.setItems(FXCollections.observableArrayList("SWG Legends"));
        galaxyComboBox.setDisable(true);

        if (properties.get() == null)
            setProperties(new Properties());

        autosaveCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> properties.get().setProperty("autosave", String.valueOf(newValue)));
        saveNagChoiceBox.selectedProperty().addListener((observable, oldValue, newValue) -> properties.get().setProperty("save.nag", String.valueOf(newValue)));

        PreferencesDialog.setController(this);
    }

    public Properties getProperties() {
        return properties.get();
    }

    public void setProperties(Properties properties) {
        this.properties.set(properties);
    }
}
