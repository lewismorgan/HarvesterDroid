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

package io.github.waverunner.harvesterdroid.ui.main;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

import io.github.waverunner.harvesterdroid.DroidProperties;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import org.controlsfx.control.StatusBar;

/**
 * Created by Waverunner on 4/3/2017.
 */
public class MainView implements FxmlView<MainViewModel>, Initializable {

  @FXML
  private StatusBar statusBar;
  @FXML
  private MenuBar menuBar;

  @InjectViewModel
  private MainViewModel viewModel;

  public void initialize(URL location, ResourceBundle resources) {
    statusBar.textProperty().bind(viewModel.statusTextProperty());

    viewModel.subscribe("StatusUpdate", (s, objects) -> Platform.runLater(() -> statusBar.setProgress(-1)));

    viewModel.subscribe("StatusUpdate.Finished", (s, objects) -> Platform.runLater(() -> statusBar.setProgress(0)));
  }

  public void save(ActionEvent actionEvent) {
    viewModel.getSaveCommand().execute();
  }

  public void preferences(ActionEvent actionEvent) {
    viewModel.getPreferencesCommand().execute();
  }

  public void close(ActionEvent actionEvent) {
    menuBar.getScene().getWindow().fireEvent(new WindowEvent(menuBar.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
  }

  public void about(ActionEvent actionEvent) {
    viewModel.getAboutCommand().execute();
  }

  public void importResources(ActionEvent actionEvent) {
    viewModel.getImportResourcesCommand().execute();
  }

  public void importSchematics(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import Schematics");
    String lastDir = DroidProperties.getString(DroidProperties.LAST_DIRECTORY);
    if (!lastDir.isEmpty()) {
      if (new File(lastDir).exists()) {
        fileChooser.setInitialDirectory(new File(lastDir));
      }
    }
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Schematics", "*.xml"));

    File result = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
    if (result != null) {
      if (result.getParent() != null) {
        DroidProperties.set(DroidProperties.LAST_DIRECTORY, result.getParent());
      }
      viewModel.importSchematics(result);
    }
  }
}