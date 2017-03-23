package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.Launcher;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.util.List;

/**
 * Created by Waverunner on 3/20/2017
 */
public class AddResourceTypeDialog extends Dialog<List<String>> {
    private FilteredList<String> filteredList;
    private CheckListView<String> listView;

    public AddResourceTypeDialog(List<String> resourceTypes) {
        super();
        setTitle("Add New Resources");
        setHeaderText("Select the resources to add");
        init();
        loadResourceTypes(resourceTypes);
    }

    private void loadResourceTypes(List<String> resourceTypes) {
        filteredList = new FilteredList<>(FXCollections.observableArrayList(resourceTypes));
        listView.setItems(filteredList);
    }

    private void init() {
        setupView();
        setupButtons();
    }

    private void setupView() {
        ((Stage)getDialogPane().getScene().getWindow()).getIcons().add(Launcher.getAppIcon());

        getDialogPane().setContent(createView());
        getDialogPane().heightProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
        getDialogPane().widthProperty().addListener((observable, oldValue, newValue) -> getDialogPane().getScene().getWindow().sizeToScene());
    }

    private Parent createView() {
        VBox root = new VBox();
        root.setSpacing(5);


        listView = new CheckListView<>();

        TextField searchTermField = new TextField();
        searchTermField.setPromptText("Enter a resource type");
        searchTermField.setPadding(new Insets(5,5,5,5));
        searchTermField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            if (!newValue.isEmpty())
                filteredList.setPredicate(str -> {
                    if(str == null) return false;

                    final int length = newValue.length();
                    if (length == 0)
                        return true;

                    for (int i = str.length() - length; i >= 0; i--) {
                        if (str.regionMatches(true, i, newValue, 0, length))
                            return true;
                    }
                    return false;
                });
            else
                filteredList.setPredicate(item -> true);
        });

        root.getChildren().addAll(searchTermField, listView);
        return root;
    }

    private void setupButtons() {
        ButtonType applyButtonType = new ButtonType("Add", ButtonBar.ButtonData.APPLY);
        getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (buttonType != applyButtonType)
                return null;

            return getSelectedResources();
        });
    }

    public List<String> getSelectedResources() {
        return listView.getCheckModel().getCheckedItems();
    }
}
