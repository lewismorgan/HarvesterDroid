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

package io.github.waverunner.harvesterdroid.app.ui.main;

import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.JSON_INVENTORY;
import static io.github.waverunner.harvesterdroid.app.HarvesterDroidData.JSON_SCHEMATICS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import io.github.waverunner.harvesterdroid.api.resource.GalaxyResource;
import io.github.waverunner.harvesterdroid.api.tracker.DataFactory;
import io.github.waverunner.harvesterdroid.app.DroidProperties;
import io.github.waverunner.harvesterdroid.app.HarvesterDroid;
import io.github.waverunner.harvesterdroid.app.data.schematics.Schematic;
import io.github.waverunner.harvesterdroid.app.ui.dialog.about.AboutDialog;
import io.github.waverunner.harvesterdroid.app.ui.dialog.preferences.PreferencesDialog;
import io.github.waverunner.harvesterdroid.app.ui.dialog.resource.ImportResourcesDialog;
import io.github.waverunner.harvesterdroid.app.ui.scopes.GalaxyScope;
import io.github.waverunner.harvesterdroid.app.ui.scopes.ResourceScope;
import io.github.waverunner.harvesterdroid.app.ui.scopes.SchematicScope;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Waverunner on 4/3/2017.
 */
@Singleton
@ScopeProvider(scopes = {SchematicScope.class, GalaxyScope.class, ResourceScope.class})
public class MainViewModel implements ViewModel {

  private static final Logger logger = LogManager.getLogger(MainViewModel.class);

  private final HarvesterDroid harvesterDroid;

  private ReadOnlyStringWrapper statusText = new ReadOnlyStringWrapper();

  private Command preferencesCommand;
  private Command saveCommand;
  private Command aboutCommand;
  private Command importResourcesCommand;

  @InjectScope
  private GalaxyScope galaxyScope;
  @InjectScope
  private ResourceScope resourceScope;
  @InjectScope
  private SchematicScope schematicScope;

  private StringProperty galaxyString = new SimpleStringProperty();
  private StringProperty resourcesString = new SimpleStringProperty();

  public MainViewModel(HarvesterDroid harvesterDroid) {
    this.harvesterDroid = harvesterDroid;
  }

  public void initialize() {
    createCommands();

    galaxyScope.subscribe(GalaxyScope.CHANGED,
        (s, objects) -> updateStatusText(harvesterDroid.getActiveGalaxy(),
            harvesterDroid.getResources().size()));
    resourceScope.subscribe(ResourceScope.UPDATED_LIST,
        (s, objects) -> updateResourceStatus(harvesterDroid.getResources().size()));

    statusText.bind(
        Bindings.concat("Galaxy: ", galaxyString, "  |  ", "Loaded Resources: ", resourcesString));
    updateStatusText(harvesterDroid.getActiveGalaxy(), harvesterDroid.getResources().size());
  }

  private void createCommands() {
    preferencesCommand = new DelegateCommand(() -> new Action() {
      @Override
      protected void action() throws Exception {
        PreferencesDialog dialog = new PreferencesDialog();
        dialog.setGalaxies(FXCollections.observableMap(harvesterDroid.getGalaxies()));
        dialog.setProperties(DroidProperties.getProperties());
        Optional<Properties> result = dialog.showAndWait();
        if (result.isPresent()) {
          Properties properties = result.get();
          DroidProperties.setProperties(properties);
          if (harvesterDroid.switchToGalaxy(properties.getProperty(DroidProperties.GALAXY))) {
            galaxyScope.publish(GalaxyScope.CHANGED);
          }
        }
      }
    });

    saveCommand = new DelegateCommand(() -> new Action() {
      @Override
      protected void action() throws Exception {
        try (FileOutputStream fileOutputStream = new FileOutputStream(JSON_INVENTORY)) {
          harvesterDroid.saveInventory(fileOutputStream);
        } catch (IOException e) {
          logger.error("Failed saving inventory", e);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(JSON_SCHEMATICS))) {
          harvesterDroid.saveSchematics(fileOutputStream);
        } catch (IOException e) {
          logger.error("Failed saving schematics", e);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(
            new File(harvesterDroid.getSavedResourcesPath()))) {
          harvesterDroid.saveResources(fileOutputStream);
        } catch (IOException e) {
          logger.error("Failed saving resources");
        }
      }
    });

    aboutCommand = new DelegateCommand(() -> new Action() {
      @Override
      protected void action() throws Exception {
        new AboutDialog().show();
      }
    });

    importResourcesCommand = new DelegateCommand(() -> new Action() {
      @Override
      protected void action() throws Exception {
        ImportResourcesDialog dialog = new ImportResourcesDialog();
        Optional<List<String>> resourceNames = dialog.showAndWait();
        resourceNames.ifPresent(strings -> {
          GalaxyResource[] toImport = new GalaxyResource[strings.size()];
          for (int i = 0; i < strings.size(); i++) {
            toImport[i] = harvesterDroid.findGalaxyResource(strings.get(i));
          }

          List<GalaxyResource> imported = new ArrayList<>();
          for (GalaxyResource galaxyResource : toImport) {
            if (galaxyResource != null) {
              imported.add(galaxyResource);
            }
          }

          resourceScope.publish(ResourceScope.IMPORT_ADDED, imported.toArray());
        });
      }
    });
  }

  private void updateStatusText(String galaxy, int resources) {
    updateGalaxyStatus(galaxy);
    updateResourceStatus(resources);
  }

  @SuppressWarnings("unchecked")
  public void importSchematics(File... schematicsFiles) {
    for (File file : schematicsFiles) {
      try {

        ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
        Set<Schematic> saved = objectMapper.readValue(new FileInputStream(file),
            new TypeReference<Set<Schematic>>() {
            }); // Prevent type erasing

        schematicScope.publish(SchematicScope.IMPORT, saved.toArray());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void updateGalaxyStatus(String galaxy) {
    galaxyString.set(galaxy);
  }

  private void updateResourceStatus(int resources) {
    resourcesString.set(String.valueOf(resources));
  }

  public Command getPreferencesCommand() {
    return preferencesCommand;
  }

  public Command getSaveCommand() {
    return saveCommand;
  }

  public Command getAboutCommand() {
    return aboutCommand;
  }

  public Command getImportResourcesCommand() {
    return importResourcesCommand;
  }

  public String getStatusText() {
    return statusText.get();
  }

  public void setStatusText(String statusText) {
    this.statusText.set(statusText);
  }

  public ReadOnlyStringProperty statusTextProperty() {
    return statusText.getReadOnlyProperty();
  }
}