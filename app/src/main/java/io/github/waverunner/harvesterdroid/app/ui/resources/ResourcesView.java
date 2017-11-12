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

package io.github.waverunner.harvesterdroid.app.ui.resources;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;

import io.github.waverunner.harvesterdroid.app.ui.items.GalaxyResourceItemView;
import io.github.waverunner.harvesterdroid.app.ui.items.GalaxyResourceItemViewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class ResourcesView implements FxmlView<ResourcesViewModel>, Initializable {

  @FXML
  private CheckBox onlyAvailableCheckbox;
  @FXML
  private ListView<GalaxyResourceItemViewModel> listView;

  @InjectViewModel
  private ResourcesViewModel viewModel;

  public void initialize(URL location, ResourceBundle resources) {

    listView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(GalaxyResourceItemView.class));
    listView.itemsProperty().bind(viewModel.resourcesProperty());
    listView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getButton() == MouseButton.PRIMARY) {
        if (event.getClickCount() >= 2) {
          viewModel.getFavoriteCommand().execute();
        }
      }
    });

    Label placeholder = new Label();
    placeholder.textProperty().bind(viewModel.statusTextProperty());
    listView.setPlaceholder(placeholder);

    viewModel.selectedProperty().bind(listView.getSelectionModel().selectedItemProperty());

    viewModel.showOnlyAvailableResourcesProperty().bind(onlyAvailableCheckbox.selectedProperty());
    onlyAvailableCheckbox.disableProperty().bind(Bindings.when(Bindings.isEmpty(listView.getItems()))
        .then(Bindings.isEmpty(viewModel.getGalaxyResources()))
        .otherwise(false));

    listView.disableProperty().bind(Bindings.when(Bindings.isEmpty(listView.getItems()))
        .then(viewModel.galaxyResourcesProperty().emptyProperty())
        .otherwise(false));
  }
}