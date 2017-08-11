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

package io.github.waverunner.harvesterdroid;

import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.ROOT_DIR;
import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.XML_INVENTORY;
import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.XML_SCHEMATICS;
import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.XML_THEMES;

import com.sun.javafx.application.LauncherImpl;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.easydi.MvvmfxEasyDIApplication;

import eu.lestard.easydi.EasyDI;

import io.github.waverunner.harvesterdroid.app.HarvesterDroid;
import io.github.waverunner.harvesterdroid.app.Watcher;
import io.github.waverunner.harvesterdroid.database.DatabaseManager;
import io.github.waverunner.harvesterdroid.api.Downloader;
import io.github.waverunner.harvesterdroid.trackers.galaxyharvester.GalaxyHarvesterDownloader;
import io.github.waverunner.harvesterdroid.ui.dialog.ExceptionDialog;
import io.github.waverunner.harvesterdroid.ui.main.MainView;
import io.github.waverunner.harvesterdroid.api.xml.XmlFactory;
import io.github.waverunner.harvesterdroid.xml.ThemesXml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends MvvmfxEasyDIApplication {
  private static boolean DEBUG = false;

  private static Stage stage;
  private static HarvesterDroid app;

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
    LauncherImpl.launchApplication(Launcher.class, LauncherPreloader.class, args);
  }

  public static Map<String, String> getResourceTypes() {
    return app.getResourceTypes();
  }

  public static HarvesterDroid getApp() {
    return app;
  }

  public static Image getAppIcon() {
    return new Image(Launcher.class.getResourceAsStream("/images/icon.png"));
  }

  @Override
  public void initMvvmfx() throws Exception {
    updateLoadingProgress("Setting up bare essentials...", -1);

    if (Files.exists(Paths.get(ROOT_DIR + "/harvesterdroid.properties"))) {
      DroidProperties.load(new FileInputStream(Paths.get(ROOT_DIR + "/harvesterdroid.properties").toFile()));
    } else {
      DroidProperties.load(getClass().getResourceAsStream("/harvesterdroid.properties"));
    }

    DEBUG = DroidProperties.getBoolean(DroidProperties.DEBUG);

    // TODO "Blank Slate" state where no tracker is loaded, will help abstract away GalaxyHarvester dependencies

    if (!new File(ROOT_DIR).exists()) {
      new File(ROOT_DIR).mkdir();
    }

    Downloader downloader = new GalaxyHarvesterDownloader(ROOT_DIR, DroidProperties.getString(DroidProperties.GALAXY));
    app.setDownloader(downloader);
    app.setLastUpdateTimestamp(Long.valueOf(DroidProperties.getString(DroidProperties.LAST_UPDATE)));
    if (new File(app.getSavedResourcesPath() + ".mv.db").exists()) {
      updateLoadingProgress("Retrieving saved resources...", -1);
      app.loadResources(app.getSavedResourcesPath());
    }

    updateLoadingProgress("Finding the latest resources...", -1);
    app.updateResources();
    updateLoadingProgress("Loading saved user data...", -1);
    if (new File(XML_SCHEMATICS).exists()) {
      app.loadSchematics(new FileInputStream(new File(XML_SCHEMATICS)));
    }
    if (new File(XML_INVENTORY).exists()) {
      // TODO Loading inventory relies on "getGalaxyResource", however it wont work if nothing is loaded
      app.loadInventory(new FileInputStream(new File(XML_INVENTORY)));
    }
    updateLoadingProgress("Punch it Chewie!", -1);
  }

  @Override
  protected void initEasyDi(EasyDI context) throws Exception {
    app = new HarvesterDroid(null, new DatabaseManager());
    context.bindInstance(HarvesterDroid.class, app);
  }

  @Override
  public void startMvvmfx(Stage primaryStage) throws Exception {
    stage = primaryStage;

    Parent root = FluentViewLoader.fxmlView(MainView.class).load().getView();
    Scene scene = new Scene(root);
    setupStylesheets(scene);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Harvester Droid");
    primaryStage.getIcons().add(getAppIcon());

    primaryStage.setFullScreen(DroidProperties.getBoolean(DroidProperties.FULLSCREEN));
    primaryStage.setWidth(DroidProperties.getDouble(DroidProperties.WIDTH));
    primaryStage.setHeight(DroidProperties.getDouble(DroidProperties.HEIGHT));

    primaryStage.show();

    primaryStage.setOnCloseRequest(e -> {
      if (DroidProperties.getBoolean(DroidProperties.SAVE_NAG)) {
        showSaveConfirmation();
      } else if (DroidProperties.getBoolean(DroidProperties.AUTOSAVE)) {
        save();
      }
    });
  }

  private void setupStylesheets(Scene scene) {
    Map<String, String> stylesheets = new HashMap<>();

    ThemesXml themesXml = XmlFactory.read(ThemesXml.class, getClass().getResourceAsStream("/themes.xml"));
    if (themesXml == null) {
      return;
    }

    themesXml.getThemes().forEach(theme -> stylesheets.put(theme.name, theme.path));

    File customThemes = new File(XML_THEMES);
    if (customThemes.exists()) {
      try {
        themesXml = XmlFactory.read(ThemesXml.class, new FileInputStream(customThemes));
        if (themesXml != null && themesXml.getThemes() != null) {
          themesXml.getThemes().forEach(theme -> stylesheets.put(theme.name, "file:///" + ROOT_DIR + "/" + theme.path));
        }
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Could not load custom themes xml!");
      }
    } else {
      themesXml.setThemes(new ArrayList<>());
      try {
        XmlFactory.write(themesXml, new FileOutputStream(XML_THEMES));
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Could not create themes.xml!");
      }
    }

    String activeTheme = DroidProperties.getString(DroidProperties.THEME);
    scene.getRoot().getStylesheets().clear();
    scene.getStylesheets().clear();

    scene.getStylesheets().add(stylesheets.get(activeTheme));

    app.setThemes(stylesheets);
    app.setActiveTheme(activeTheme);
  }

  @Override
  public void stopMvvmfx() throws Exception {
    Watcher.shutdown();
    saveProperties();
    app.shutdown();
  }

  private void showSaveConfirmation() {
    Alert save = new Alert(Alert.AlertType.INFORMATION);
    save.setTitle("Harvester Droid");
    save.setContentText("Would you like to save your changes?");
    save.setHeaderText(null);
    save.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);

    Optional<ButtonType> result = save.showAndWait();
    if (result.isPresent() && result.get().equals(ButtonType.YES)) {
      save();
    }
  }

  private void save() {
    try {
      app.saveInventory(new FileOutputStream(new File(XML_INVENTORY)));
      app.saveSchematics(new FileOutputStream(new File(XML_SCHEMATICS)));
      app.saveResources();
      DroidProperties.set(DroidProperties.LAST_UPDATE, app.getCurrentResourceTimestamp());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    saveProperties();
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
