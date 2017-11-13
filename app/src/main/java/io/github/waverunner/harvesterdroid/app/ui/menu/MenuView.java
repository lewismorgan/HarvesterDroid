package io.github.waverunner.harvesterdroid.app.ui.menu;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

import io.github.waverunner.harvesterdroid.app.DroidProperties;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

public class MenuView implements FxmlView<MenuViewModel> {

  @InjectViewModel
  private MenuViewModel viewModel;

  public void initialize() {

  }

  public void importSchematics(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import Schematics");
    String lastDir = DroidProperties.getString(DroidProperties.LAST_DIRECTORY);
    if (!lastDir.isEmpty()) {
      if (new File(lastDir).exists()) {
        fileChooser.setInitialDirectory(new File(lastDir));
      }
    }
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Schematics", "*.xml"));

    File result = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
    if (result != null) {
      if (result.getParent() != null) {
        DroidProperties.set(DroidProperties.LAST_DIRECTORY, result.getParent());
      }
      viewModel.importSchematics(result);
    }
  }

  public void save() {
    viewModel.getSaveCommand().execute();
  }

  public void preferences() {
    viewModel.getPreferencesCommand().execute();
  }

  public void close(ActionEvent actionEvent) {
    Node node = ((Node) actionEvent.getTarget());
    node.getScene().getWindow().fireEvent(new WindowEvent(node.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
  }

  public void about() {
    viewModel.getAboutCommand().execute();
  }

  public void importResources() {
    viewModel.getImportResourcesCommand().execute();
  }
}