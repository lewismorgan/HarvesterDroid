package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.waverunnah.swg.harvesterdroid.Launcher;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Waverunner on 3/20/2017
 */
public class AddResourceTypeDialog extends Dialog<List<String>> {
    private FilteredList<String> filteredList;
    private CheckListView<String> listView;

    private Map<String, String> resourceTypes;

    private List<String> selectedCache = new ArrayList<>();

    public AddResourceTypeDialog(Map<String, String> resourceTypes) {
        super();
        setTitle("Add New Resources");
        setHeaderText("Select the resources to add");
        init();
        loadResourceTypes(resourceTypes);
    }

    private void loadResourceTypes(Map<String, String> resourceTypes) {
        this.resourceTypes = resourceTypes;
        List<String> sorted = new ArrayList<>(resourceTypes.keySet());
        Collections.sort(sorted);

        filteredList = new FilteredList<>(FXCollections.observableArrayList(sorted));
        filteredList.predicateProperty().addListener((observable, oldValue, newValue) -> {
            for (String cached : selectedCache) {
                listView.getCheckModel().check(cached);
            }
        });
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
        listView.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        listView.setMinSize(400, 400);
        listView.checkModelProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            newValue.getCheckedItems().addListener((ListChangeListener<? super String>) c -> {
                // Filtered list is used so gotta keep track of the checks
                while (c.next()) {
                    if (c.getAddedSize() > 0) {
                        for (String added : c.getAddedSubList()) {
                            if (!selectedCache.contains(added))
                                selectedCache.add(added);
                        }
                    } if (c.getRemovedSize() > 0) {
                        for (String removed : c.getRemoved()) {
                            if (selectedCache.contains(removed)) {
                                selectedCache.remove(removed);
                            }
                        }
                    }
                }
            });
        });

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

        root.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        root.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
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
        List<String> selection = new ArrayList<>();
        for (String s : selectedCache) {
            selection.add(resourceTypes.get(s));
        }
        return selection;
    }
}
