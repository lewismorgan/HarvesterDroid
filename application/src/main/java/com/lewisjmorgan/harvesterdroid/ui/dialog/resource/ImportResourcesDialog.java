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

package com.lewisjmorgan.harvesterdroid.ui.dialog.resource;

import com.lewisjmorgan.harvesterdroid.ui.dialog.BaseDialog;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 * Created by Waverunner on 4/7/17.
 */
public class ImportResourcesDialog extends BaseDialog<List<String>> {
  private static ButtonType importButtonType = new ButtonType("Import");

  @FXML
  private TextArea textAreaResources;

  public ImportResourcesDialog() {
    super("Import Resources");
  }

  @Override
  protected void createDialog() {
    setResultConverter(buttonType -> {
      if (buttonType != importButtonType) {
        return new ArrayList<>();
      }
      return createResourceNameList();
    });
  }

  private List<String> createResourceNameList() {
    List<String> names = new ArrayList<>();
    String text = textAreaResources.getText();

    text = text.replace(" ", ",");
    text = text.replace("\n", ",");
    text = text.replace(";", ",");
    String[] commas = text.split(",");

    for (String comma : commas) {
      if (!comma.isEmpty() && !names.contains(comma)) {
        names.add(comma);
      }
    }

    return names;
  }

  @Override
  protected ButtonType[] getButtonTypes() {
    return new ButtonType[] {
        importButtonType,
        ButtonType.CLOSE
    };
  }

  @Override
  protected boolean isController() {
    return true;
  }
}
