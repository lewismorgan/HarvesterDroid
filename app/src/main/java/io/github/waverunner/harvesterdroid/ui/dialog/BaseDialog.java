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

package io.github.waverunner.harvesterdroid.ui.dialog;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Abstract class for boilerplate code in the creation of dialogs for HarvesterDroid.
 *
 * <p>Created by Waverunner on 3/29/2017.
 */
public abstract class BaseDialog<R> extends Dialog<R> {
  public BaseDialog(String title) {
    super();
    setTitle(title);
    init();
  }

  private void init() {
    try {
      Parent root;
      if (isController()) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getClass().getSimpleName().toLowerCase().replace("dialog", "_dialog.fxml")));
        loader.setController(this);
        root = loader.load();
      } else {
        root = FXMLLoader.load(getClass().getResource(getClass().getSimpleName().toLowerCase().replace("dialog", "_dialog.fxml")));
      }

      ((Stage) getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
      if (root != null) {
        getDialogPane().setContent(root);
      }
      getDialogPane().heightProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
      getDialogPane().widthProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());

      getDialogPane().getButtonTypes().addAll(getButtonTypes());

      createDialog();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void createDialog() {
  }

  protected abstract ButtonType[] getButtonTypes();

  protected abstract boolean isController();
}
