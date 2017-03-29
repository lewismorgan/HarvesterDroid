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

package com.waverunnah.swg.harvesterdroid.gui.dialog;


import com.waverunnah.swg.harvesterdroid.Launcher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutDialog extends Dialog {

	public AboutDialog() {
		super();
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		setTitle("About");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("about_dialog.fxml"));
            if (!(root instanceof VBox))
                return;

            ((Stage)getDialogPane().getScene().getWindow()).getIcons().add(Launcher.getAppIcon());
            getDialogPane().setContent(root);
            getDialogPane().heightProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
            getDialogPane().widthProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());

            getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        } catch (IOException e) {
            ExceptionDialog.display(e);
        }
	}
}
