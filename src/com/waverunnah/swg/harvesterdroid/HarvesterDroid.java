package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.xml.app.CurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.galacticharvester.HarvesterCurrentResourcesXml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HarvesterDroid extends Application {
	private static HarvesterDroid instance;

	private static Stage stage;

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private CurrentResourcesXml currentResourcesXml;

	private SchematicsXml schematicsXml;
	private InventoryXml inventoryXml;

	@Override
	public void init() throws Exception {
		// TODO Loading Screen w/ proper initializations
		instance = this;

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Downloader.downloadXmls();

		// TODO Preferences determines what CurrentResourcesXml subclass to use
		currentResourcesXml = new HarvesterCurrentResourcesXml(documentBuilder);
		currentResourcesXml.load(new FileInputStream("./data/current48.xml"));

		schematicsXml = new SchematicsXml(documentBuilder);
		schematicsXml.load(new FileInputStream("./data/user/schematics.xml"));

		inventoryXml = new InventoryXml(documentBuilder);
		inventoryXml.load(new FileInputStream("./data/user/inventory.xml"));
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
	    stage = primaryStage;


        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void save() {
	    try {
		    instance.schematicsXml.save(new File("data/user/schematics.xml"));
		    instance.inventoryXml.save(new File("data/user/inventory.xml"));
	    } catch (TransformerException | FileNotFoundException e) {
		    e.printStackTrace();
	    }
    }

    public static void main(String[] args) {
        launch(args);
    }

	public static Stage getStage() {
		return stage;
	}

	public static CurrentResourcesXml getCurrentResourcesXml() {
		return instance.currentResourcesXml;
	}

	public static SchematicsXml getSchematicsXml() {
		return instance.schematicsXml;
	}

	public static InventoryXml getInventoryXml() {
		return instance.inventoryXml;
	}
}
