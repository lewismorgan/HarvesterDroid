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

package io.github.waverunner.harvesterdroid.ui.inventory;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;

import io.github.waverunner.harvesterdroid.ui.items.GalaxyResourceItemView;
import io.github.waverunner.harvesterdroid.ui.items.GalaxyResourceItemViewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class InventoryView implements FxmlView<InventoryViewModel>, Initializable {
  @FXML
  private Button removeButton;
  @FXML
  private Button addButton;

  @InjectViewModel
  private InventoryViewModel viewModel;

  @FXML
  private ListView<GalaxyResourceItemViewModel> listView;

  public void initialize(URL location, ResourceBundle resources) {
    listView.disableProperty().bind(viewModel.inventoryProperty().emptyProperty());
    listView.itemsProperty().bind(viewModel.inventoryProperty());
    listView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(GalaxyResourceItemView.class));
    viewModel.selectedProperty().bind(listView.getSelectionModel().selectedItemProperty());
    removeButton.disableProperty().bind(viewModel.getRemoveCommand().executableProperty().not());
  }

  public void removeSelectedResource() {
    viewModel.getRemoveCommand().execute();
  }

  public void addGalaxyResource() {
    viewModel.getAddCommand().execute();
  }
}