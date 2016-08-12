package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SchematicDialogController extends VBox implements Initializable {

	@FXML
	TextField nameField;
	@FXML
	ComboBox<String> professionComboBox;
	@FXML
	ListView<String> resourceList;
	@FXML
	TableView attributesTable;

	private Schematic schematic;

	public void readSchematic(Schematic schematic) {
		this.schematic = schematic;

		System.out.println("Reading schematic");
		nameField.textProperty().bind(schematic.nameProperty());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Controller set");
		SchematicDialog.setController(this);
	}

	public Schematic getSchematic() {
		return schematic;
	}
}
