package io.github.waverunner.harvesterdroid.app.views.resource;

import de.saxsys.mvvmfx.FxmlView;
import io.github.waverunner.harvesterdroid.app.models.resource.InterestingResourcesViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;

/**
 * Created by lewis on 11/15/17.
 */
public class InterestingResourcesView extends TitledPane implements FxmlView<InterestingResourcesViewModel> {

  @FXML
  public CheckBox onlyAvailableCheckbox;

  @FXML
  public ResourceListView resourcesListView;
}
