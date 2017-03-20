package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HarvesterDroid {
	// TODO Status messages
	// TODO Move intensive methods to a Task

	private Downloader downloader;

	private ListProperty<GalaxyResource> inventory;
	private ListProperty<GalaxyResource> resources;
	private ListProperty<Schematic> schematics;
	private ListProperty<String> groups;

	private FilteredList<GalaxyResource> filteredInventory;
	private FilteredList<GalaxyResource> filteredResources;
	private FilteredList<Schematic> filteredSchematics;

	private StringProperty activeGroup;
	private ObjectProperty<Schematic> selectedSchematic;

	public HarvesterDroid() {
		this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	public HarvesterDroid(Collection<Schematic> schematics, Collection<GalaxyResource> resources, Collection<GalaxyResource> inventory) {
		init(schematics, resources, inventory);
		createListeners();
	}

	private void init(Collection<Schematic> schematics, Collection<GalaxyResource> resources, Collection<GalaxyResource> inventory) {
		this.inventory = new SimpleListProperty<>(FXCollections.observableArrayList(inventory));
		filteredInventory = new FilteredList<>(this.inventory.get(), galaxyResource -> true);
		this.resources = new SimpleListProperty<>(FXCollections.observableArrayList(resources));
		filteredResources = new FilteredList<>(this.resources.get(), galaxyResource -> false);
		this.schematics = new SimpleListProperty<>(FXCollections.observableArrayList(schematic -> new javafx.beans.Observable[]{schematic.nameProperty()}));
		this.schematics.addAll(schematics);
		filteredSchematics = new FilteredList<>(this.schematics.get(), schematic -> true);
		selectedSchematic = new SimpleObjectProperty<>(null);
		initGroups();
	}

	private void initGroups() {
		groups = new SimpleListProperty<>(FXCollections.observableArrayList());
		activeGroup = new SimpleStringProperty(null);

		schematics.forEach(schematic -> {
			if (!groups.contains(schematic.getGroup()))
				groups.add(schematic.getGroup());
		});
	}

	private void createListeners() {
		activeGroup.addListener(this::onActiveGroupChanged);
		selectedSchematic.addListener(this::onSchematicSelected);
		schematics.addListener((ListChangeListener<? super Schematic>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					onSchematicsAdded(c.getAddedSubList());
				} else if (c.wasRemoved()) {
					onSchematicsRemoved(c.getRemoved());
				}
			}
		});
		inventory.addListener((ListChangeListener<? super GalaxyResource>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					c.getAddedSubList().forEach(galaxyResource -> {
						if (!resources.contains(galaxyResource))
							resources.add(galaxyResource);
					});
				}
			}
		});
	}

	private void onSchematicsAdded(List<? extends Schematic> addedSchematics) {
		addedSchematics.stream().filter(schematic -> !groups.contains(schematic.getGroup()))
				.forEach(match -> groups.add(match.getGroup()));
		filterResourcesForSchematic(selectedSchematic.get());
		if (activeGroup.get() != null && groups.size() > 1 && schematics.size() > 1)
			filteredSchematics.setPredicate(schematic -> schematic.getGroup().equals(activeGroup.get()));
	}

	private void onSchematicsRemoved(List<? extends Schematic> removedSchematics) {
		List<String> used = new ArrayList<>();

		schematics.forEach(schematic -> used.add(schematic.getGroup()));
		List<String> toRemove = groups.stream().filter(group -> !used.contains(group)).collect(Collectors.toList());
		if (!toRemove.isEmpty()) {
			toRemove.forEach(groups::remove);
		}
	}

	private void onSchematicSelected(ObservableValue<? extends Schematic> observable, Schematic oldValue, Schematic newValue) {
		if (newValue != null)
			filterResourcesForSchematic(newValue);
		else filteredResources.setPredicate(galaxyResource -> false);
	}

	private void onActiveGroupChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (oldValue == null)
			return;

		// Filtered list doesn't like to be sorted when there is only one item in it, it'll throw index exceptions
		if(newValue == null || (newValue.length() == 0 && schematics.size() > 1)) {
			filteredSchematics.setPredicate(schematic -> true);
		} else if (schematics.size() > 1) {
			filteredSchematics.setPredicate(schematic -> schematic.getGroup().equals(newValue));
		}
	}

	private void filterResourcesForSchematic(Schematic schematic) {
		if (schematic == null || schematic.isIncomplete() || resources.isEmpty())
			return;

		List<GalaxyResource> bestResources = getBestResourcesList(schematic);
		filteredResources.setPredicate(bestResources::contains);

	}

	private List<GalaxyResource> getBestResourcesList(Schematic schematic) {
		List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
		schematic.getResources().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources);
				if (bestResource != null)
					bestResources.add(bestResource);
			}
		});
		return bestResources;
	}

	public GalaxyResource collectBestResourceForSchematic(Schematic schematic, List<GalaxyResource> galaxyResources) {
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

	public float getResourceWeightedAverage(List<Schematic.Modifier> modifierList, GalaxyResource resource) {
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

	public List<GalaxyResource> findGalaxyResourcesById(String id) {
		List<String> resourceGroups = Launcher.getResourceGroups(id);
		Collection<GalaxyResource> galaxyResourceList = Launcher.getCurrentResources();
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

	public void save() throws IOException, TransformerException {
		Launcher.save(inventory.stream().map(GalaxyResource::getName).collect(Collectors.toList()), schematics.get());
	}

	public ObservableList<GalaxyResource> getInventory() {
		return inventory.get();
	}

	public ListProperty<GalaxyResource> inventoryProperty() {
		return inventory;
	}

	public ObservableList<GalaxyResource> getResources() {
		return resources.get();
	}

	public ListProperty<GalaxyResource> resourcesProperty() {
		return resources;
	}

	public ObservableList<Schematic> getSchematics() {
		return schematics.get();
	}

	public ListProperty<Schematic> schematicsProperty() {
		return schematics;
	}

	public ObservableList<String> getGroups() {
		return groups.get();
	}

	public ListProperty<String> groupsProperty() {
		return groups;
	}

	public FilteredList<GalaxyResource> getFilteredInventory() {
		return filteredInventory;
	}

	public FilteredList<GalaxyResource> getFilteredResources() {
		return filteredResources;
	}

	public FilteredList<Schematic> getFilteredSchematics() {
		return filteredSchematics;
	}

	public StringProperty activeGroupProperty() {
		return activeGroup;
	}

	public ObjectProperty<Schematic> selectedSchematicProperty() {
		return selectedSchematic;
	}
}
