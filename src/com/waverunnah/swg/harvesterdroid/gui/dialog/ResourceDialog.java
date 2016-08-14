package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.gui.ResourceListItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ResourceDialog extends Dialog<ResourceListItem> {

	private static ResourceDialogController controller;

	public ResourceDialog() {
		this(new ResourceListItem());
	}

	public ResourceDialog(ResourceListItem resourceItem) {
		init();
		if (controller != null)
			controller.editResourceItem(resourceItem);
	}

	private void init() {
		setupView();
		setupButtons();
	}

	private void setupView() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("resource_dialog.fxml"));
			if (!(root instanceof VBox))
				return;

			getDialogPane().setContent(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupButtons() {
		ButtonType saveButtonType = new ButtonType("Add", ButtonBar.ButtonData.APPLY);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		setResultConverter(buttonType -> {
			if (buttonType != saveButtonType)
				return null;
			controller.getResourceListItem().refresh();
			return controller.getResourceListItem();
		});
	}

	public static void setController(ResourceDialogController controller) {
		ResourceDialog.controller = controller;
	}
}
