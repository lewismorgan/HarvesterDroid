/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.app.Watcher;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Launcher extends Application {
    private static boolean DEBUG = false;
    // TODO Finish refactoring business logic into HarvesterDroid
    private static final Map<String, List<String>> resourceGroups = new HashMap<>();
    public static String ROOT_DIR = System.getProperty("user.home") + "/.harvesterdroid";
    private static String XML_SCHEMATICS = ROOT_DIR + "/schematics.xml";
    private static String XML_INVENTORY = ROOT_DIR + "/inventory.xml";
    private static Launcher instance;
    private static Stage stage;
    private HarvesterDroid app;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (Platform.isFxApplicationThread() && !DEBUG) {
                ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                exceptionDialog.show();
            } else {
                // TODO Logging to a file
                e.printStackTrace();
            }
        });

        launch(args);
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

    @Override
    public void init() throws Exception {
        updateLoadingProgress("Setting up bare essentials...", 0.1);
        instance = this;

        if (Files.exists(Paths.get(ROOT_DIR + "/harvesterdroid.properties")))
            DroidProperties.load(new FileInputStream(Paths.get(ROOT_DIR + "/harvesterdroid.properties").toFile()));
        else DroidProperties.load(getClass().getResourceAsStream("/harvesterdroid.properties"));

        DEBUG = DroidProperties.getBoolean(DroidProperties.DEBUG);

        // TODO Decide what downloader to use based on preferences
        Downloader downloader = new GalaxyHarvesterDownloader(DroidProperties.getString(DroidProperties.GALAXY));
        app = new HarvesterDroid(XML_SCHEMATICS, XML_INVENTORY, "galaxyharvester", downloader);

        if (!new File(ROOT_DIR).exists())
            new File(ROOT_DIR).mkdir();

        updateLoadingProgress("Finding the latest resources...", -1);
        app.updateResources();
        updateLoadingProgress("Loading saved data...", -1);
        app.loadSavedData();
        updateLoadingProgress("Punch it Chewie!", -1);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(getAppIcon());

        primaryStage.setFullScreen(DroidProperties.getBoolean(DroidProperties.FULLSCREEN));
        primaryStage.setWidth(DroidProperties.getDouble(DroidProperties.WIDTH));
        primaryStage.setHeight(DroidProperties.getDouble(DroidProperties.HEIGHT));

        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            if (DroidProperties.getBoolean(DroidProperties.SAVE_NAG))
                showSaveConfirmation();
            else if (DroidProperties.getBoolean(DroidProperties.AUTOSAVE))
                app.save();
            close();
        });
    }

    private void showSaveConfirmation() {
        Alert save = new Alert(Alert.AlertType.INFORMATION);
        save.setTitle("Harvester Droid");
        save.setContentText("Would you like to save your changes?");
        save.setHeaderText(null);
        save.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);

        Optional<ButtonType> result = save.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            app.save();
        }
    }

    private void close() {
        Watcher.shutdown();
        saveProperties();
        app.shutdown();
    }

    private void saveProperties() {
        DroidProperties.set(DroidProperties.HEIGHT, stage.getHeight());
        DroidProperties.set(DroidProperties.WIDTH, stage.getWidth());
        DroidProperties.set(DroidProperties.FULLSCREEN, stage.isFullScreen());

        try {
            DroidProperties.save(new FileOutputStream(System.getProperty("user.home")
                    + "/.harvesterdroid/harvesterdroid.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateLoadingProgress(String status, double value) {
        notifyPreloader(new PreloaderStatusNotification(status, value));
    }
}
