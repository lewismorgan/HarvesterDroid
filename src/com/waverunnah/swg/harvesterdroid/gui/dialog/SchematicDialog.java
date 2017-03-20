package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SchematicDialog extends Dialog<Schematic> {

	private static SchematicDialogController controller;

	public SchematicDialog() {
		this(new Schematic());
	}

	public SchematicDialog(Schematic schematic) {
		init();
		if (controller != null)
			controller.readSchematic(schematic);
	}

	private void init() {
		setTitle("Schematic Editor");
		setupView();
		setupButtons();
	}

	private void setupView() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("schematic_dialog.fxml"));
			if (!(root instanceof VBox))
				return;

			((Stage)getDialogPane().getScene().getWindow()).getIcons().add(Launcher.getAppIcon());
			getDialogPane().setContent(root);
			getDialogPane().heightProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
			getDialogPane().widthProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
		} catch (IOException e) {
			ExceptionDialog.display(e);
		}
	}

	private void setupButtons() {
		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.APPLY);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
		saveButton.setDefaultButton(true);

		setResultConverter(buttonType -> {
			if (buttonType != saveButtonType)
				return null;
			return controller.getSchematic();
		});
	}

	public static void setController(SchematicDialogController controller) {
		SchematicDialog.controller = controller;
	}
}
