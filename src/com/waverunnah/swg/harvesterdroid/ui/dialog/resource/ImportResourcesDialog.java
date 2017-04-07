package com.waverunnah.swg.harvesterdroid.ui.dialog.resource;

import com.waverunnah.swg.harvesterdroid.ui.dialog.BaseDialog;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Waverunner on 4/7/17
 */
public class ImportResourcesDialog extends BaseDialog<List<String>> {
    private static ButtonType importButtonType = new ButtonType("Import");

    @FXML
    private TextArea textAreaResources;

    public ImportResourcesDialog() {
        super("Import Resources");
    }

    @Override
    protected void createDialog() {
        setResultConverter(buttonType -> {
            if (buttonType != importButtonType)
                return new ArrayList<>();
            return createResourceNameList();
        });
    }

    private List<String> createResourceNameList() {
        List<String> names = new ArrayList<>();
        String text = textAreaResources.getText();

        text = text.replace(" ", ",");
        text = text.replace("\n", ",");
        text = text.replace(";", ",");
        String[] commas = text.split(",");

        for (String comma : commas) {
            if (!comma.isEmpty() && !names.contains(comma))
                names.add(comma);
        }

        return names;
    }

    @Override
    protected ButtonType[] getButtonTypes() {
        return new ButtonType[] {
                importButtonType,
                ButtonType.CLOSE
        };
    }

    @Override
    protected boolean isController() {
        return true;
    }
}
