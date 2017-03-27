package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.utils.Watcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Launcher extends Application {
	private static final boolean IGNORE_UNCAUGHT_EXCEPTIONS = true;
	// TODO Finish refactoring business logic into HarvesterDroid

	public static String ROOT_DIR = System.getProperty("user.home").replace("\\", "/") + "/.harvesterdroid";
	private static String XML_SCHEMATICS = ROOT_DIR + "/schematics.xml";
	private static String XML_INVENTORY = ROOT_DIR + "/inventory.xml";

	private static Launcher instance;

	private static Stage stage;

	private HarvesterDroid app;

	private static final Map<String, List<String>> resourceGroups = new HashMap<>();

	@Override
	public void init() throws Exception {
		updateLoadingProgress("Setting up bare essentials...", 0.1);
		instance = this;

		Downloader downloader = new GalaxyHarvesterDownloader();

		// TODO Decide what downloader to use based on preferences
		app = new HarvesterDroid(XML_SCHEMATICS, XML_INVENTORY, downloader);

		if (!new File(ROOT_DIR).exists())
			new File(ROOT_DIR).mkdir();

		updateLoadingProgress("Finding the latest resources...", -1);
		app.updateResources();
        updateLoadingProgress("Loading saved data...", -1);
		app.loadSavedData();
		updateLoadingProgress("Punch it Chewie!", -1.0);
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
	    stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
		primaryStage.getIcons().add(getAppIcon());
        primaryStage.show();

		primaryStage.setOnCloseRequest(e -> close());
    }

	private void close() {
		Watcher.shutdown();
		try {
		    Alert save = new Alert(Alert.AlertType.INFORMATION);
		    save.setTitle("Harvester Droid");
		    save.setContentText("Would you like to save your changes?");
		    save.setHeaderText(null);
		    save.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);

            Optional<ButtonType> result = save.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.YES))
                app.save();
		} catch (IOException | TransformerException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
	    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
	    	if (Platform.isFxApplicationThread() && !IGNORE_UNCAUGHT_EXCEPTIONS) {
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

	public static Stage getStage() {
		return stage;
	}

	public static Map<String, String> getResourceTypes() {
		return instance.app.getResourceTypes();
	}

	public static HarvesterDroid getApp() {
		return instance.app;
	}

    public static Image getAppIcon() {
        return new Image(Launcher.class.getResourceAsStream("/images/icon.png"));
    }
}
