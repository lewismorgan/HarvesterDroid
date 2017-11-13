package io.github.waverunner.harvesterdroid.app.ui.menu;

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
import io.github.waverunner.harvesterdroid.app.ui.main.MainViewModel;
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

import javafx.collections.FXCollections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ScopeProvider(scopes = {GalaxyScope.class, ResourceScope.class, SchematicScope.class})
public class MenuViewModel implements ViewModel {
  private static final Logger logger = LogManager.getLogger(MainViewModel.class);

  private final HarvesterDroid harvesterDroid;

  private Command preferencesCommand;
  private Command saveCommand;
  private Command aboutCommand;
  private Command importResourcesCommand;

  @InjectScope
  GalaxyScope galaxyScope;
  @InjectScope
  ResourceScope resourceScope;
  @InjectScope
  SchematicScope schematicScope;

  public MenuViewModel(HarvesterDroid harvesterDroid) {
    this.harvesterDroid = harvesterDroid;
  }

  public void initialize() {
    createCommands();
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(harvesterDroid.getSavedResourcesPath()))) {
          harvesterDroid.saveResources(fileOutputStream);
        } catch (IOException e) {
          logger.error("Failed saving resources", e);
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

  public void importSchematics(File... schematicsFiles) {
    for (File file : schematicsFiles) {
      try {

        ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
        Set<Schematic> saved = objectMapper.readValue(new FileInputStream(file),
            new TypeReference<Set<Schematic>>() {
            }); // Prevent type erasing

        schematicScope.publish(SchematicScope.IMPORT, saved.toArray());
      } catch (FileNotFoundException e) {
        logger.warn("Could not find file {}", file);
      } catch (IOException e) {
        logger.error("Thrown trying to import schematics", e);
      }
    }
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
}