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
