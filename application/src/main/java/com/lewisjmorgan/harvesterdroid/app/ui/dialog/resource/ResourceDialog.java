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
import com.lewisjmorgan.harvesterdroid.app.ui.dialog.BaseDialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class ResourceDialog extends BaseDialog<GalaxyResource> {

  private static ButtonType ADD = new ButtonType("Add", ButtonBar.ButtonData.APPLY);
  private static ResourceDialogController controller;

  public ResourceDialog() {
    super("Add Resource");
  }

  @Override
  protected ButtonType[] getButtonTypes() {
    return new ButtonType[] {
        ADD,
        ButtonType.CANCEL
    };
  }

  @Override
  protected void createDialog() {
    setResultConverter(buttonType -> {
      if (buttonType != ADD) {
        return null;
      }
      controller.retrieveStats();
      return controller.getGalaxyResource();
    });
  }

  @Override
  protected boolean isController() {
    return false;
  }

  public static void setController(ResourceDialogController controller) {
    ResourceDialog.controller = controller;
  }
}
