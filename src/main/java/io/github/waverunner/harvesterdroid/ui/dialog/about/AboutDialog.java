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

package io.github.waverunner.harvesterdroid.ui.dialog.about;

import io.github.waverunner.harvesterdroid.ui.dialog.BaseDialog;

import javafx.scene.control.ButtonType;

public class AboutDialog extends BaseDialog {

  public AboutDialog() {
    super("About");
  }

  @Override
  protected ButtonType[] getButtonTypes() {
    return new ButtonType[] {
        ButtonType.CLOSE
    };
  }

  @Override
  protected boolean isController() {
    return false;
  }
}
