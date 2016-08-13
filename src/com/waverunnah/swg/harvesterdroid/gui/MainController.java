package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.SchematicDialog;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.controlsfx.control.StatusBar;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {
	// TODO Split each panel into it's own component (inventory, schematics, best resources)
	// TODO Move intensive methods to a Task

	private List<String> loadedResources = new ArrayList<>();
	private ObservableList<ResourceListItem> resourceListItems;
	private FilteredList<ResourceListItem> filteredResourceListItems;

	private ObservableList<Schematic> schematicsList;
	private FilteredList<Schematic> filteredSchematicList;

	@FXML
	ListView<Schematic> schematicsListView;
	@FXML
	ListView<String> inventoryListView;
	@FXML
	ListView<ResourceListItem> bestResourcesListView;
	@FXML
	ComboBox<String> professionComboBox;
	@FXML
	StatusBar statusBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initResources();
		initSchematics();
		initGroups();
	}

	private void initResources() {
		resourceListItems = FXCollections.observableArrayList();

		filteredResourceListItems = new FilteredList<>(resourceListItems, resourceListItem -> false);
		bestResourcesListView.setItems(filteredResourceListItems);
	}

	private void initGroups() {
		professionComboBox.setItems(FXCollections.observableArrayList(HarvesterDroid.getProfessions()));
		professionComboBox.getItems().add(0, "any"); // Any filter
		professionComboBox.getSelectionModel().select(0);

		professionComboBox.getSelectionModel().selectedItemProperty().addListener(observable -> {
			String filter = professionComboBox.getSelectionModel().getSelectedItem();

			if(filter == null || filter.length() == 0 || filter.equals("any")) {
				filteredSchematicList.setPredicate(s -> true);
			}
			else {
				filteredSchematicList.setPredicate(s -> s.getGroup().equals(filter));
			}
		});
	}

	private void initSchematics() {
		schematicsListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Schematic>() {
			@Override
			public void onChanged(Change<? extends Schematic> c) {
				while (c.next()) {
					if (c.wasAdded() && c.getAddedSize() == 1) {
						List<? extends Schematic> change = c.getAddedSubList();
						change.forEach(schematic -> updateBestResourceList(schematic));
					} else if (c.wasRemoved()) {
						schematicsListView.getSelectionModel().clearSelection();
					}
				}
			}
		});

		schematicsList = FXCollections.observableArrayList(schematic -> new Observable[]{schematic.nameProperty()});
		schematicsList.setAll(HarvesterDroid.getSchematics());

		filteredSchematicList = new FilteredList<>(schematicsList, schematic -> true);
		schematicsListView.setItems(filteredSchematicList);
	}

	public void editSelectedSchematic() {
		if (schematicsListView.getSelectionModel().getSelectedItem() == null)
			return;

		displaySchematicDialog(schematicsListView.getSelectionModel().getSelectedItem());
	}

	public void displaySchematicDialog() {
		SchematicDialog dialog = new SchematicDialog();
		dialog.setTitle("Create Schematic");
		Optional<Schematic> result = dialog.showAndWait();
		if (!result.isPresent())
			return;

		schematicsList.add(result.get());
	}

	public void displaySchematicDialog(Schematic schematic) {
		SchematicDialog dialog = new SchematicDialog(schematic);
		dialog.setTitle("Edit Schematic");
		Optional<Schematic> result = dialog.showAndWait();
		result.ifPresent(this::updateBestResourceList);
	}

	private void updateBestResourceList(Schematic schematic) {
		updateStatusBar("Updating Best Resources List");
		bestResourcesListView.setDisable(false);

		List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
		schematic.getResources().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources);
				if (bestResource != null) {
					bestResources.add(bestResource);
				} else {
					System.out.println("No resource is available for " + id);
				}
			}
		});

		createResourceListItems(bestResources);
		filteredResourceListItems.setPredicate(resourceListItem -> bestResources.contains(resourceListItem.getGalaxyResource()));
		refreshStatusBar();
	}

	private void createResourceListItems(List<GalaxyResource> galaxyResources) {
		for (GalaxyResource galaxyResource : galaxyResources) {
			if (loadedResources.contains(galaxyResource.getName()))
				continue;

			System.out.println("Creating resourceListItem for " + galaxyResource);
			ResourceListItem item = new ResourceListItem();
			item.setGalaxyResource(galaxyResource);
			resourceListItems.add(item);
			loadedResources.add(galaxyResource.getName());
		}
	}

	private GalaxyResource collectBestResourceForSchematic(Schematic schematic, List<GalaxyResource> galaxyResources) {
		// TODO: Account for resources in "inventory"

		GalaxyResource ret = null;
		float weightedAvg = -1;
		List<Schematic.Modifier> modifiers = schematic.getModifiers();

		for (GalaxyResource galaxyResource : galaxyResources) {
			float galaxyResourceAvg = getResourceWeightedAverage(modifiers, galaxyResource);
			if (ret == null || weightedAvg == -1) {
				ret = galaxyResource;
				weightedAvg = galaxyResourceAvg;
			} else if (weightedAvg < galaxyResourceAvg) {
				ret = galaxyResource;
				weightedAvg = galaxyResourceAvg;
			}
		}

		return ret;
	}

	private float getResourceWeightedAverage(List<Schematic.Modifier> modifierList, GalaxyResource resource) {
		// TODO Calculate w/ expertise resource bonus (+40 pts) if have it

		// TODO Account for resource caps

		float average = 0;
		for (Schematic.Modifier modifier : modifierList) {
			if (resource.hasAttribute(modifier.getName())) {
				average = average + (resource.getAttribute(modifier.getName()) * modifier.getValue());
			}
		}

		return average;
	}

	private List<GalaxyResource> findGalaxyResourcesById(String id) {
		List<String> resourceGroups = Downloader.getResourceGroups(id);
		if (resourceGroups != null) {
			List<GalaxyResource> master = new ArrayList<>();
			for (String group : resourceGroups) {
				master.addAll(HarvesterDroid.getGalaxyResources().stream()
						.filter(galaxyResource -> galaxyResource.getResourceType().startsWith(group))
						.collect(Collectors.toList()));
			}
			return master;
		} else {
			return HarvesterDroid.getGalaxyResources().stream().filter(galaxyResource ->
					galaxyResource.getResourceType().equals(id) || galaxyResource.getResourceType().startsWith(id)
			).collect(Collectors.toList());
		}
	}

	public void updateStatusBar(String status) {
		statusBar.setText(status);
		statusBar.setProgress(-1);
	}

	public void refreshStatusBar() {
		statusBar.setText("Idle");
		statusBar.setProgress(0);
	}
}
