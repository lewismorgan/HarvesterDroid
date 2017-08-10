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

package io.github.waverunner.harvesterdroid.ui.dialog.resource;

import io.github.waverunner.harvesterdroid.ui.dialog.BaseDialog;
import io.github.waverunner.harvesterdroid.ui.items.GalaxyResourceItemView;
import io.github.waverunner.harvesterdroid.ui.items.GalaxyResourceItemViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 4/7/17
 */
public class NewSpawnsDialog extends BaseDialog implements Initializable {

    @FXML
    private ListView<GalaxyResourceItemViewModel> resourcesListView;

    public NewSpawnsDialog() {
        super("New Resources!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourcesListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(GalaxyResourceItemView.class));
    }

    @Override
    protected ButtonType[] getButtonTypes() {
        return new ButtonType[]{
                ButtonType.CLOSE
        };
    }

    @Override
    protected boolean isController() {
        return true;
    }

    public void setNewSpawns(List<GalaxyResourceItemViewModel> newSpawns) {
        resourcesListView.setItems(FXCollections.observableArrayList(newSpawns));
    }
}
