package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SchematicDialog extends Dialog<Schematic> {

	private static SchematicDialogController controller;

	public SchematicDialog() {
		setTitle("Edit Schematic");
		setupView();
		setupButtons();
	}

	public SchematicDialog(Schematic schematic) {
		this();
		if (controller != null)
			controller.readSchematic(schematic);
	}

	private void setupView() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("schematic_dialog.fxml"));
			if (!(root instanceof VBox))
				return;

			getDialogPane().setContent(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupButtons() {
		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.APPLY);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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
