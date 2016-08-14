package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.callbacks.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ResourceDialog;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.controlsfx.control.StatusBar;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {
	// TODO Split each panel into it's own component (inventory, schematics, best resources)
	// TODO Move intensive methods to a Task

	private ObservableList<GalaxyResource> inventoryListItems;
	private FilteredList<GalaxyResource> filteredInventoryListItems;

	private ObservableList<GalaxyResource> resourceListItems;
	private FilteredList<GalaxyResource> filteredResourceListItems;

	private ObservableList<Schematic> schematicsList;
	private FilteredList<Schematic> filteredSchematicList;

	@FXML
	ListView<Schematic> schematicsListView;
	@FXML
	ListView<GalaxyResource> inventoryListView;
	@FXML
	ListView<GalaxyResource> bestResourcesListView;
	@FXML
	ComboBox<String> professionComboBox;
	@FXML
	StatusBar statusBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initResources();
		initInventory();
		initSchematics();
		initGroups();
	}

	private void initInventory() {
		inventoryListView.setCellFactory(param -> new GalaxyResourceListCell());
		inventoryListItems = FXCollections.observableArrayList();

		filteredInventoryListItems = new FilteredList<>(inventoryListItems, inventoryListItem -> true);
		inventoryListView.setItems(filteredInventoryListItems);

		inventoryListItems.addListener((ListChangeListener<? super GalaxyResource>) c -> {
			while (c.next()) {
				if (c.wasAdded() && c.getAddedSize() > 0) {
					List<String> inventoryData = HarvesterDroid.getInventoryXml().getInventory();
					List<? extends GalaxyResource> added = c.getAddedSubList();
					added.forEach(galaxyResource -> {
						if (!inventoryData.contains(galaxyResource.getName()))
							inventoryData.add(galaxyResource.getName());
					});
				} else if (c.wasRemoved() && c.getRemovedSize() > 0) {
					List<String> inventoryData = HarvesterDroid.getInventoryXml().getInventory();
					List<? extends GalaxyResource> removed = c.getRemoved();
					removed.forEach(galaxyResource -> inventoryData.remove(galaxyResource.getName()));
				}
			}
		});

		updateInventoryFromXml();
	}

	public void updateInventoryFromXml() {
		List<String> resourceNames = new ArrayList<>(HarvesterDroid.getInventoryXml().getInventory());
		resourceNames.forEach(name -> {
			GalaxyResource galaxyResource = HarvesterDroid.getCurrentResourcesXml().getGalaxyResources().get(name);
			if (galaxyResource != null) {
				if (!inventoryListItems.contains(galaxyResource)) {
					inventoryListView.setDisable(false);
					inventoryListItems.add(galaxyResource);
					if (!resourceListItems.contains(galaxyResource))
						resourceListItems.add(galaxyResource);
				}
			} else {
				// TODO Add single resource data retrieval
			}
		});
	}

	private void initResources() {
		bestResourcesListView.setCellFactory(param -> new GalaxyResourceListCell());
		resourceListItems = FXCollections.observableArrayList();

		filteredResourceListItems = new FilteredList<>(resourceListItems, resourceListItem -> false);
		bestResourcesListView.setItems(filteredResourceListItems);
	}

	private void initGroups() {
		professionComboBox.setItems(FXCollections.observableArrayList(HarvesterDroid.getSchematicsXml().getProfessionList()));
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
		schematicsList.setAll(HarvesterDroid.getSchematicsXml().getSchematicsList());

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

		List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
		schematic.getResources().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources);
				if (bestResource != null) {
					System.out.println("Adding best resource " + bestResource);
					bestResources.add(bestResource);
				} else {
					System.out.println("No resource is available for " + id);
				}
			}
		});

		if (bestResources.size() == 0) {
			bestResourcesListView.setPlaceholder(new Label("No resources are available for this schematic"));
			bestResourcesListView.setDisable(true);
		} else {
			bestResourcesListView.setDisable(false);
			bestResources.stream().filter(galaxyResource -> !resourceListItems.contains(galaxyResource))
					.forEach(resourceListItems::add);
			filteredResourceListItems.setPredicate(bestResources::contains);
		}

		refreshStatusBar();
	}

	private GalaxyResource collectBestResourceForSchematic(Schematic schematic, List<GalaxyResource> galaxyResources) {
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
			int value = resource.getAttribute(modifier.getName());
			if (value == -1)
				continue;
			average = average + (resource.getAttribute(modifier.getName()) * modifier.getValue());
		}

		return average;
	}

	private List<GalaxyResource> findGalaxyResourcesById(String id) {
		List<String> resourceGroups = Downloader.getResourceGroups(id);
		Collection<GalaxyResource> galaxyResourceList = HarvesterDroid.getCurrentResourcesXml().getGalaxyResourceList();
		if (resourceGroups != null) {
			List<GalaxyResource> master = new ArrayList<>();
			for (String group : resourceGroups) {
				master.addAll(galaxyResourceList.stream()
						.filter(galaxyResource -> galaxyResource.getResourceType().startsWith(group)
								|| galaxyResource.getResourceType().equals(group))
						.collect(Collectors.toList()));
			}
			return master;
		} else {
			return galaxyResourceList.stream().filter(galaxyResource ->
					galaxyResource.getResourceType().equals(id) || galaxyResource.getResourceType().startsWith(id)
			).collect(Collectors.toList());
		}
	}

	public void addInventoryResource() {
		GalaxyResource selectedItem = bestResourcesListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			inventoryListView.setDisable(false);
			if (!inventoryListItems.contains(selectedItem))
				inventoryListItems.add(selectedItem);
		} else {
			ResourceDialog dialog = new ResourceDialog();
			dialog.setTitle("New Inventory Resource");
			Optional<GalaxyResource> result = dialog.showAndWait();
			if (!result.isPresent() || inventoryListItems.contains(result.get()))
				return;
			inventoryListView.setDisable(false);
			inventoryListItems.add(result.get());
		}
	}

	public void removeInventoryResource() {
		GalaxyResource selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		if (inventoryListItems.size() - 1 == 0)
			inventoryListView.setDisable(true);
		inventoryListItems.remove(selectedItem);
	}

	public void save() {
		HarvesterDroid.save();
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
