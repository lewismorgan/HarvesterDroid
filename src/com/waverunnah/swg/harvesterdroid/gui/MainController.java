package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.callbacks.GalaxyResourceListCell;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ResourceDialog;
import com.waverunnah.swg.harvesterdroid.gui.dialog.SchematicDialog;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import org.controlsfx.control.StatusBar;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {
	// TODO Split each panel into it's own component (inventory, schematics, best resources)
	// TODO Move intensive methods to a Task
	// TODO Move business logic to it's own class independent of the controller -- controller only for binding

	private ObservableList<GalaxyResource> inventoryListItems;
	private FilteredList<GalaxyResource> filteredInventoryListItems;

	private ObservableList<GalaxyResource> resourceListItems;
	private FilteredList<GalaxyResource> filteredResourceListItems;

	private ObservableList<Schematic> schematicsList;
	private FilteredList<Schematic> filteredSchematicList;

	private ObservableList<String> groupsList;

	@FXML
	TitledPane bestResourcesPane;
	@FXML
	ListView<Schematic> schematicsListView;
	@FXML
	ListView<GalaxyResource> inventoryListView;
	@FXML
	ListView<GalaxyResource> bestResourcesListView;
	@FXML
	ComboBox<String> groupComboBox;
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
		inventoryListItems = FXCollections.observableArrayList();

		inventoryListView.setCellFactory(param -> new GalaxyResourceListCell());
		inventoryListView.disableProperty().bind(Bindings.isEmpty(inventoryListItems));

		filteredInventoryListItems = new FilteredList<>(inventoryListItems, inventoryListItem -> true);
		inventoryListView.setItems(filteredInventoryListItems);

		updateInventory();
	}

	public void updateInventory() {
		List<String> resourceNames = new ArrayList<>(HarvesterDroid.getInventory());

		resourceNames.forEach(name -> {
			GalaxyResource galaxyResource = HarvesterDroid.getCurrentResourcesMap().get(name);
			if (galaxyResource == null) {
				try {
					galaxyResource = Downloader.downloadGalaxyResource(name);
				} catch (IOException e) {
					ExceptionDialog.display(e);
				}
			}

			if (!inventoryListItems.contains(galaxyResource)) {
				inventoryListView.setDisable(false);
				inventoryListItems.add(galaxyResource);
				if (!resourceListItems.contains(galaxyResource))
					resourceListItems.add(galaxyResource);
			}
		});
	}

	private void initResources() {
		resourceListItems = FXCollections.observableArrayList();

		bestResourcesPane.setText("Best Resources as of " + HarvesterDroid.getLastUpdate());
		bestResourcesListView.disableProperty().bind(Bindings.isEmpty(resourceListItems));
		bestResourcesListView.setCellFactory(param -> new GalaxyResourceListCell());

		filteredResourceListItems = new FilteredList<>(resourceListItems, resourceListItem -> false);
		bestResourcesListView.setItems(filteredResourceListItems);
	}

	private void initGroups() {
		groupsList = FXCollections.observableArrayList();
		schematicsList.addListener((ListChangeListener<? super Schematic>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					List<? extends Schematic> addedSubList = c.getAddedSubList();
					addedSubList.stream().filter(schematic -> !groupsList.contains(schematic.getGroup()))
							.forEach(match -> groupsList.add(match.getGroup()));
				} else if (c.wasRemoved()) {
					List<? extends Schematic> removed = c.getRemoved();
					Map<String, Integer> toRemove = new HashMap<>(removed.size());
					for (Schematic schematic : removed) {
						int count = 0;
						if (toRemove.containsKey(schematic.getGroup()))
							count = toRemove.get(schematic.getGroup());
						toRemove.put(schematic.getGroup(), count++);
					}
					toRemove.forEach((group, count) -> {
						if (count > 1)
							groupsList.remove(group);
					});
				}
			}
		});

		groupComboBox.setItems(groupsList);
		groupComboBox.getItems().add(0, "any"); // Any filter
		groupComboBox.getSelectionModel().select(0);

		groupComboBox.getSelectionModel().selectedItemProperty().addListener(observable -> {
			String filter = groupComboBox.getSelectionModel().getSelectedItem();

			if(filter == null || filter.length() == 0 || filter.equals("any")) {
				filteredSchematicList.setPredicate(s -> true);
			}
			else {
				filteredSchematicList.setPredicate(s -> s.getGroup().equals(filter));
			}
		});
	}

	private void initSchematics() {
		schematicsList = FXCollections.observableArrayList(schematic -> new Observable[]{schematic.nameProperty()});
		schematicsList.setAll(HarvesterDroid.getSchematics());

		schematicsListView.disableProperty().bind(Bindings.isEmpty(schematicsList));
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
		Schematic schematic = result.get();
		if (!schematic.isIncomplete())
			schematicsList.add(schematic);
		else updateInfoText("Could not save schematic as it was missing some entries!");
	}

	public void displaySchematicDialog(Schematic schematic) {
		SchematicDialog dialog = new SchematicDialog(schematic);
		dialog.setTitle("Edit Schematic");
		Optional<Schematic> result = dialog.showAndWait();
		result.ifPresent(this::updateBestResourceList);
	}

	private void updateBestResourceList(Schematic schematic) {
		if (schematic == null || schematic.isIncomplete())
			return;

		updateStatusBar("Updating Best Resources List");
		List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
		schematic.getResources().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources);
				if (bestResource != null)
					bestResources.add(bestResource);
			}
		});

		if (bestResources.size() == 0) {
			bestResourcesListView.setPlaceholder(new Label("No resources are available for this schematic"));
		} else {
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
		Collection<GalaxyResource> galaxyResourceList = HarvesterDroid.getCurrentResources();
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
			if (!inventoryListItems.contains(selectedItem))
				inventoryListItems.add(selectedItem);
		} else {
			ResourceDialog dialog = new ResourceDialog();
			dialog.setTitle("New Inventory Resource");
			Optional<GalaxyResource> result = dialog.showAndWait();
			if (!result.isPresent() || inventoryListItems.contains(result.get()))
				return;
			inventoryListItems.add(result.get());
		}
	}

	public void removeInventoryResource() {
		GalaxyResource selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		inventoryListItems.remove(selectedItem);
	}

	public void save() {
		HarvesterDroid.save(
				inventoryListItems.stream().map(GalaxyResource::getName).collect(Collectors.toList()), schematicsList);
	}

	public void updateStatusBar(String status) {
		statusBar.setText(status);
		statusBar.setProgress(-1);
	}

	public void updateInfoText(String info) {
		statusBar.setText(info);
		statusBar.setProgress(0);
	}

	public void refreshStatusBar() {
		statusBar.setText("Idle");
		statusBar.setProgress(0);
	}
}
