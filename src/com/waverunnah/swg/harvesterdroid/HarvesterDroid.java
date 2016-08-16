package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.xml.app.CurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.galacticharvester.HarvesterCurrentResourcesXml;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HarvesterDroid extends Application {
	private static String XML_SCHEMATICS = "./data/user/schematics.xml";
	private static String XML_INVENTORY = "./data/user/inventory.xml";

	private List<String> resourceTypes = new ArrayList<>();
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

		BufferedReader bufferedReader = new BufferedReader(new FileReader("./data/types"));
		resourceTypes = bufferedReader.lines().collect(Collectors.toList());

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Downloader.downloadCurrentResources();

		// TODO Preferences determines what CurrentResourcesXml subclass to use
		if (Files.exists(Paths.get("./data/current_resources.dl"))) {
			currentResourcesXml = new HarvesterCurrentResourcesXml(documentBuilder);
			currentResourcesXml.load(new FileInputStream("./data/current_resources.dl"));
		} else {
			currentResourcesXml = new CurrentResourcesXml();
		}

		schematicsXml = new SchematicsXml(documentBuilder);
		if (Files.exists(Paths.get(XML_SCHEMATICS)))
			schematicsXml.load(new FileInputStream(XML_SCHEMATICS));

		inventoryXml = new InventoryXml(documentBuilder);
		if (Files.exists(Paths.get(XML_INVENTORY)))
			inventoryXml.load(new FileInputStream(XML_INVENTORY));
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
	    stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

		primaryStage.setOnCloseRequest(e -> {
			save();
			Platform.exit();
			System.exit(0);
		});
    }

    public static void save() {
	    try {
		    instance.schematicsXml.save(new File("data/user/schematics.xml"));
		    instance.inventoryXml.save(new File("data/user/inventory.xml"));
	    } catch (TransformerException | IOException e) {
		    ExceptionDialog.display(e);
	    }
    }

    public static void main(String[] args) {
	    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
		    ExceptionDialog exceptionDialog = new ExceptionDialog(e);
		    exceptionDialog.show();
	    });

        launch(args);
    }

	public static Stage getStage() {
		return stage;
	}

	public static Collection<GalaxyResource> getCurrentResources() {
		return instance.currentResourcesXml.getGalaxyResourceList();
	}

	public static Map<String, GalaxyResource> getCurrentResourcesMap() {
		return instance.currentResourcesXml.getGalaxyResources();
	}

	public static String getLastUpdate() {
		return instance.currentResourcesXml.getTimestamp();
	}

	public static List<Schematic> getSchematics() {
		return instance.schematicsXml.getSchematicsList();
	}

	public static List<String> getInventory() {
		return instance.inventoryXml.getInventory();
	}

	public static List<String> getResourceTypes() {
		return instance.resourceTypes;
	}

	public static void save(List<String> inventoryListItems, List<Schematic> schematicsList) {
		instance.schematicsXml.setSchematics(schematicsList);
		instance.inventoryXml.setInventory(inventoryListItems);
	}
}
