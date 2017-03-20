package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.utils.Watcher;
import com.waverunnah.swg.harvesterdroid.xml.app.CurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import com.waverunnah.swg.harvesterdroid.xml.galacticharvester.HarvesterCurrentResourcesXml;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Launcher extends Application {
	public static String ROOT_DIR = System.getProperty("user.home").replace("\\", "/") + "/.harvesterdroid";
	private static String XML_SCHEMATICS = ROOT_DIR + "/schematics.xml";
	private static String XML_INVENTORY = ROOT_DIR + "/inventory.xml";

	private List<String> resourceTypes = new ArrayList<>();
	private static Launcher instance;

	private static Stage stage;

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private CurrentResourcesXml currentResourcesXml;

	private SchematicsXml schematicsXml;
	private InventoryXml inventoryXml;
	private HarvesterDroid app;

	@Override
	public void init() throws Exception {
		updateLoadingProgress("Setting up bare essentials...", 0.1);
		instance = this;

		if (!new File(ROOT_DIR).exists())
			new File(ROOT_DIR).mkdirs();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream("/com/waverunnah/swg/harvesterdroid/data/raw/types")));
		resourceTypes = bufferedReader.lines().collect(Collectors.toList());
		Collections.sort(resourceTypes);
		updateLoadingProgress("Finding the latest resources...", 0.5);
		currentResourcesXml = new HarvesterCurrentResourcesXml(factory.newDocumentBuilder());

		// TODO Preferences determines what CurrentResourcesXml subclass to use
		if (Files.exists(Paths.get(ROOT_DIR + "/current_resources.dl"))) {
			currentResourcesXml.load(new FileInputStream(ROOT_DIR + "/current_resources.dl"));
			if (resourcesNeedUpdate(currentResourcesXml.getTimestamp()))
				GalaxyHarvesterDownloader.downloadCurrentResources();
		} else {
			GalaxyHarvesterDownloader.downloadCurrentResources();
		}

		currentResourcesXml.load(new FileInputStream(ROOT_DIR + "/current_resources.dl"));

		updateLoadingProgress("Grabbing your preferences...", 1);
		schematicsXml = new SchematicsXml(factory.newDocumentBuilder());
		if (Files.exists(Paths.get(XML_SCHEMATICS)))
			schematicsXml.load(new FileInputStream(XML_SCHEMATICS));

		inventoryXml = new InventoryXml(factory.newDocumentBuilder());
		if (Files.exists(Paths.get(XML_INVENTORY)))
			inventoryXml.load(new FileInputStream(XML_INVENTORY));

		updateLoadingProgress("Loading...", -1.0);
		app = new HarvesterDroid(getSchematics(), getCurrentResources(), getInventoryGalaxyResources());
	}

	private boolean resourcesNeedUpdate(String time) throws ParseException, IOException, SAXException, ParserConfigurationException {
		DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z");
		Date timestamp = dateFormat.parse(time);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
		LocalDateTime plusHours = from.plusHours(12);
		return now.isAfter(plusHours);
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
	    stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("gui/images/icon.png")));
        primaryStage.show();

		primaryStage.setOnCloseRequest(e -> {
			Watcher.shutdown();
			try {
				app.save();
			} catch (IOException | TransformerException e1) {
				e1.printStackTrace();
			}
			save();
		});
    }

    public static void save() {
	    try {
		    instance.schematicsXml.save(new File(XML_SCHEMATICS));
		    instance.inventoryXml.save(new File(XML_INVENTORY));
	    } catch (TransformerException | IOException e) {
		    ExceptionDialog.display(e);
	    }
    }

    public static void main(String[] args) {
	    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
	    	if (Platform.isFxApplicationThread()) {
			    ExceptionDialog exceptionDialog = new ExceptionDialog(e);
			    exceptionDialog.show();
		    } else {
		    	// TODO Logging to a file
		    	e.printStackTrace();
		    }
	    });

        launch(args);
    }

	private void updateLoadingProgress(String status, double value) {
		notifyPreloader(new PreloaderStatusNotification(status, value));
	}

	private List<GalaxyResource> getInventoryGalaxyResources() {
		List<GalaxyResource> inventory = new ArrayList<>();
		Map<String, GalaxyResource> currentResources = getCurrentResourcesMap();
		inventoryXml.getInventory().forEach(name -> {
			if (!currentResources.containsKey(name)) {
				try {
					GalaxyResource galaxyResource = GalaxyHarvesterDownloader.downloadGalaxyResource(name);
					if (galaxyResource != null && galaxyResource.getName().equals(name))
						inventory.add(galaxyResource);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				inventory.add(currentResources.get(name));
			}
		});
		return inventory;
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
		return instance.schematicsXml.getSchematics();
	}

	public static List<String> getInventory() {
		return instance.inventoryXml.getInventory();
	}

	public static List<String> getResourceTypes() {
		return instance.resourceTypes;
	}

	public static void save(List<String> inventoryListItems, List<Schematic> schematicsList) throws IOException, TransformerException {
		instance.schematicsXml.setSchematics(schematicsList);
		instance.inventoryXml.setInventory(inventoryListItems);

		instance.schematicsXml.save(new File(XML_SCHEMATICS));
		instance.inventoryXml.save(new File(XML_INVENTORY));
	}

	public static HarvesterDroid getApp() {
		return instance.app;
	}
}
