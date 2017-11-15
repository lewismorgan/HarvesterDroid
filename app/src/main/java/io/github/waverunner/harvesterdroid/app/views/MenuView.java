package io.github.waverunner.harvesterdroid.app.views;

import de.saxsys.mvvmfx.FxmlView;
import io.github.waverunner.harvesterdroid.app.models.MainViewModel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.NotImplementedException;

public class MenuView implements FxmlView<MainViewModel> {

  public void importSchematics() {
    throw new NotImplementedException("TODO: Schematics refactor");
  }

  public void save() {
    throw new NotImplementedException("TODO: Saving");
  }

  public void preferences() {
    throw new NotImplementedException("TODO: Preferences");
  }

  public void close(ActionEvent actionEvent) {
    Node node = ((Node) actionEvent.getTarget());
    node.getScene().getWindow().fireEvent(new WindowEvent(node.getScene().getWindow(),
        WindowEvent.WINDOW_CLOSE_REQUEST));
  }

  public void about() {
    throw new NotImplementedException("TODO: About");
  }

  public void importResources() {
    throw new NotImplementedException("TODO: Resource Import");
  }
}