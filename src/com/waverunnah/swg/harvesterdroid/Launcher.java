package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.utils.Watcher;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Launcher extends Application {
	public static String ROOT_DIR = System.getProperty("user.home").replace("\\", "/") + "/.harvesterdroid";
	private static String XML_SCHEMATICS = ROOT_DIR + "/schematics.xml";
	private static String XML_INVENTORY = ROOT_DIR + "/inventory.xml";

	private List<String> resourceTypes = new ArrayList<>();
	private static Launcher instance;

	private static Stage stage;

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private Downloader downloader;

	private SchematicsXml schematicsXml;
	private InventoryXml inventoryXml;
	private HarvesterDroid app;

	private static final Map<String, List<String>> resourceGroups = new HashMap<>();

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

		testDownloadData(); // TODO Use preferences to specify downloader class

		updateLoadingProgress("Grabbing your preferences...", 1);
		schematicsXml = new SchematicsXml(factory.newDocumentBuilder());
		if (Files.exists(Paths.get(XML_SCHEMATICS)))
			schematicsXml.load(new FileInputStream(XML_SCHEMATICS));

		inventoryXml = new InventoryXml(factory.newDocumentBuilder());
		if (Files.exists(Paths.get(XML_INVENTORY)))
			inventoryXml.load(new FileInputStream(XML_INVENTORY));

		updateLoadingProgress("Loading...", -1.0);
		app = new HarvesterDroid(getSchematics(), downloader.getCurrentResources(), getInventoryGalaxyResources());
	}

	private void testDownloadData() {
		downloader = new GalaxyHarvesterDownloader();
		try {
			downloader.downloadCurrentResources();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Map<String, GalaxyResource> currentResources = downloader.getCurrentResourcesMap();
		inventoryXml.getInventory().forEach(name -> {
			if (!currentResources.containsKey(name)) {
				try {
					// TODO Use a generic downloadGalaxyResource method
					GalaxyResource galaxyResource = ((GalaxyHarvesterDownloader) downloader).downloadGalaxyResource(name);
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
		return instance.downloader.getCurrentResources();
	}

	public static String getLastUpdate() {
		if (instance.downloader.getCurrentResourcesTimestamp() == null)
			return null;
		return instance.downloader.getCurrentResourcesTimestamp().toString();
	}

	// TODO Delete method once abstraction is finished on Downloader class
	public static GalaxyResource downloadGalaxyResource(String galaxyResource) {
		try {
			return ((GalaxyHarvesterDownloader) instance.downloader).downloadGalaxyResource(galaxyResource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	// TODO Refactor resource groups
	static {
		// This only needs to be done for the resources that do not follow the proper hierarchy naming convention
		try {
			CodeSource src = Launcher.class.getProtectionDomain().getCodeSource();
			String path = "com/waverunnah/swg/harvesterdroid/data/raw/groups/";
			if (src != null) {
				ZipInputStream zip = new ZipInputStream(src.getLocation().openStream());
				ZipEntry entry = zip.getNextEntry();

				if (entry == null) {
					File dir = new File(src.getLocation().getPath() + path);
					File[] files = dir.listFiles();
					for (File file : files != null ? files : new File[0]) {
						populateResourceGroup(file.getAbsolutePath());
					}
				} else {
					while (entry != null) {
						if (entry.getName() != null)
							populateResourceGroup(entry.getName());
						entry = zip.getNextEntry();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void populateResourceGroup(String file) {
		List<String> resourceGroup = new ArrayList<>();
		resourceGroup.add(file.substring(file.lastIndexOf("\\") + 1));
		try {
			try (Stream<String> stream = Files.lines(Paths.get(file))) {
				stream.forEach(resourceGroup::add);
			}
		} catch (IOException e) {
			ExceptionDialog.display(e);
		}
		resourceGroups.put(file.substring(file.lastIndexOf("\\") + 1), resourceGroup);
	}

	public static List<String> getResourceGroups(String group) {
		return resourceGroups.get(group);
	}
}
