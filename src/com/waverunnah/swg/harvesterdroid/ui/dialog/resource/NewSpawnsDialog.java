package com.waverunnah.swg.harvesterdroid.ui.dialog.resource;

import com.waverunnah.swg.harvesterdroid.ui.dialog.BaseDialog;
import com.waverunnah.swg.harvesterdroid.ui.items.GalaxyResourceItemView;
import com.waverunnah.swg.harvesterdroid.ui.items.GalaxyResourceItemViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 4/7/17
 */
public class NewSpawnsDialog extends BaseDialog implements Initializable {

    @FXML
    private ListView<GalaxyResourceItemViewModel> resourcesListView;

    public NewSpawnsDialog() {
        super("New Resources!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourcesListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(GalaxyResourceItemView.class));
    }

    @Override
    protected ButtonType[] getButtonTypes() {
        return new ButtonType[] {
                ButtonType.CLOSE
        };
    }

    @Override
    protected boolean isController() {
        return true;
    }

    public void setNewSpawns(List<GalaxyResourceItemViewModel> newSpawns) {
        resourcesListView.setItems(FXCollections.observableArrayList(newSpawns));
    }
}
