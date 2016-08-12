package com.waverunnah.swg.harvesterdroid.gui;

import com.waverunnah.swg.harvesterdroid.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.gui.dialog.SchematicDialog;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

	private List<GalaxyResource> galaxyResourceList;
	private Map<String, ResourceListItem> resourceListItemCache = new HashMap<>();

	@FXML
	ListView<Schematic> schematicsList;
	@FXML
	ListView<String> inventoryList;
	@FXML
	ListView<ResourceListItem> bestResourcesList; // TODO: Use filters instead of adding/removing
	@FXML
	ComboBox<String> professionComboBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<ResourceListItem> resourceListItems = bestResourcesList.getItems();

		galaxyResourceList = HarvesterDroid.getGalaxyResources();

		ObservableList<Schematic> data = FXCollections.observableArrayList(HarvesterDroid.getSchematics());

		FilteredList<Schematic> filteredSchematicList = new FilteredList<>(data, schematic -> true);
		schematicsList.setItems(filteredSchematicList);

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

		schematicsList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Schematic>() {
			@Override
			public void onChanged(Change<? extends Schematic> c) {
				cacheNewResourceListItems();
				bestResourcesList.getItems().clear();

				while (c.next()) {
					if (c.wasAdded()) {
						List<? extends Schematic> change = c.getAddedSubList();
						updateBestResourceList(change);
					} else if (c.wasRemoved()) {
						clearBestResourceList();
					}
				}
			}
		});

		for (int i = 0; i < 10; i++) {
			GalaxyResource galaxyResource = galaxyResourceList.get(i);
			ResourceListItem item = new ResourceListItem();
			item.setGalaxyResource(galaxyResource);
			resourceListItems.add(item);
		}
	}

	public void editSelectedSchematic() {
		if (schematicsList.getSelectionModel().getSelectedItem() == null)
			return;

		displaySchematicDialog(schematicsList.getSelectionModel().getSelectedItem());
	}

	public void displaySchematicDialog() {
		SchematicDialog dialog = new SchematicDialog();
		Optional<Schematic> result = dialog.showAndWait();
		if (!result.isPresent())
			return;

		schematicsList.getItems().add(result.get());
	}

	public void displaySchematicDialog(Schematic schematic) {
		SchematicDialog dialog = new SchematicDialog(schematic);
		dialog.showAndWait();
	}

	private void cacheNewResourceListItems() {
		bestResourcesList.getItems().forEach(resourceListItem -> {
			if (!resourceListItemCache.containsKey(resourceListItem.getGalaxyResource().getName()))
				resourceListItemCache.put(resourceListItem.getGalaxyResource().getName(), resourceListItem);
		});
	}

	private void updateBestResourceList(List<? extends Schematic> schematics) {
		schematics.forEach(schematic -> schematic.getResources().keySet().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources, schematic.getResources().get(id));
				if (bestResource != null) {
					createResourceListItem(bestResource);
				} else {
					System.out.println("No resource is available for " + id);
				}
			}
		}));
	}

	private void clearBestResourceList() {
		bestResourcesList.getItems().clear();
	}

	private void createResourceListItem(GalaxyResource galaxyResource) {
		// Don't need any duplicates!
		for (ResourceListItem resourceListItem : bestResourcesList.getItems()) {
			if (resourceListItem.getGalaxyResource() == galaxyResource) {
				return;
			}
		}
		if (!resourceListItemCache.containsKey(galaxyResource.getName())) {
			System.out.println("Creating resourceListItem for " + galaxyResource);
			ResourceListItem item = new ResourceListItem();
			item.setGalaxyResource(galaxyResource);
			bestResourcesList.getItems().add(item);
		} else {
			bestResourcesList.getItems().add(resourceListItemCache.get(galaxyResource.getName()));
		}
	}

	private GalaxyResource collectBestResourceForSchematic(Schematic schematic, List<GalaxyResource> galaxyResources, int count) {
		// TODO: Account for resources in "inventory"

		GalaxyResource ret = null;
		float weightedAvg = -1;
		Map<String, Float> modifiers = schematic.getModifiers();

		for (GalaxyResource galaxyResource : galaxyResources) {
			float galaxyResourceAvg = getResourceWeightedAverage(modifiers, galaxyResource, count);
			// TODO Check if resource has all attributes
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

	private float getResourceWeightedAverage(Map<String, Float> attributes, GalaxyResource resource, int count) {
		// TODO Calculate w/ expertise resource bonus (+40 pts) if have it

		// TODO Account for resource caps

		Map<String, Integer> calcWeights = new HashMap<>();
		attributes.forEach((attribute, weight) -> {
			if (resource.hasAttribute(attribute)) {
				int result = ((resource.getAttribute(attribute) * count) / count);

				calcWeights.put(attribute, result);
			}
		});


		float average = 0;
		for (Map.Entry<String, Integer> entry : calcWeights.entrySet()) {
			average = average + attributes.get(entry.getKey()) * entry.getValue();
		}

		return average;
	}

	private List<GalaxyResource> findGalaxyResourcesById(String id) {
		List<String> resourceGroups = Downloader.getResourceGroups(id);
		if (resourceGroups != null) {
			List<GalaxyResource> master = new ArrayList<>();
			for (String group : resourceGroups) {
				master.addAll(galaxyResourceList.stream().filter(galaxyResource -> galaxyResource.getResourceType().startsWith(group)).collect(Collectors.toList()));
			}
			return master;
		} else {
			return galaxyResourceList.stream().filter(galaxyResource ->
					galaxyResource.getResourceType().equals(id) || galaxyResource.getResourceType().startsWith(id)
			).collect(Collectors.toList());
		}
	}
}
