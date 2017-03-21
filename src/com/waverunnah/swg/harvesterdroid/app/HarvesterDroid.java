package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HarvesterDroid {
	// TODO Status messages
	// TODO Move intensive methods to a Task

	private final String schematicsXmlPath;
	private final String inventoryXmlPath;

	private final Downloader downloader;

	private SchematicsXml schematicsXml;
	private InventoryXml inventoryXml;

	private ListProperty<GalaxyResource> inventory;
	private SimpleListProperty<GalaxyResource> resources;
	private ListProperty<Schematic> schematics;
	private ListProperty<String> groups;

	private FilteredList<GalaxyResource> filteredInventory;
	private FilteredList<GalaxyResource> filteredResources;
	private FilteredList<Schematic> filteredSchematics;

	private StringProperty activeGroup;
	private ObjectProperty<Schematic> activeSchematic;
	private BooleanProperty displayingAllGroups;

	public HarvesterDroid(String schematicsXmlPath, String inventoryXmlPath, Downloader downloader) {
		this.schematicsXmlPath = schematicsXmlPath;
		this.inventoryXmlPath = inventoryXmlPath;
		this.downloader = downloader;
		init(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
		activeSchematic = new SimpleObjectProperty<>(null);
		displayingAllGroups = new SimpleBooleanProperty(true);
		initGroups();
	}

	private void initGroups() {
		groups = new SimpleListProperty<>(FXCollections.observableArrayList());
		activeGroup = new SimpleStringProperty();
	}

	private void createListeners() {
		activeGroup.addListener(this::onActiveGroupChanged);
		activeSchematic.addListener(this::onSchematicSelected);
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
		displayingAllGroups.addListener((observable, oldValue, newValue) -> {
			if (newValue)
				filteredSchematics.setPredicate(schematic -> newValue);
			else {
				filteredSchematics.setPredicate(schematic -> schematic.getGroup().equals(activeGroup.get()));
			}
		});
	}

	private void onSchematicsAdded(List<? extends Schematic> addedSchematics) {
		addedSchematics.stream().filter(schematic -> !groups.contains(schematic.getGroup()))
				.forEach(match -> groups.add(match.getGroup()));
		filterResourcesForSchematic(activeSchematic.get());
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
		float average = 0;

        for (Schematic.Modifier modifier : modifierList) {
            float value = resource.getAttribute(modifier.getName());
            if (value == -1)
                continue;

            average += (value * (float) modifier.getValue() / 100);
        }

        average = average / 1000;
        return average;
	}

	public List<GalaxyResource> findGalaxyResourcesById(String id) {
		List<String> resourceGroups = Launcher.getResourceGroups(id);
		Collection<GalaxyResource> galaxyResourceList = resources.get();
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

	public GalaxyResource getGalaxyResourceByName(String name) {
        Optional<GalaxyResource> optional = resources.get().stream().filter(galaxyResource -> galaxyResource.getName().equals(name)).findFirst();
        return optional.orElse(null);
	}

    public String getLastUpdate() {
		if (downloader.getCurrentResourcesTimestamp() == null)
			return null;
		return downloader.getCurrentResourcesTimestamp().toString();
	}

	public void save() throws IOException, TransformerException {
		schematicsXml.setSchematics(getSchematics());
		inventoryXml.setInventory(getInventory().stream().map(GalaxyResource::getName).collect(Collectors.toList()));

		schematicsXml.save(new File(schematicsXmlPath));
		inventoryXml.save(new File(inventoryXmlPath));
	}

	public void updateResources() {
		try {
			downloader.downloadCurrentResources();
			resources.clear();
			resources.addAll(downloader.getCurrentResources());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadSavedData() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			schematicsXml = new SchematicsXml(factory.newDocumentBuilder());
			inventoryXml = new InventoryXml(factory.newDocumentBuilder());

			if (Files.exists(Paths.get(schematicsXmlPath)))
				schematicsXml.load(new FileInputStream(schematicsXmlPath));
			if (Files.exists(Paths.get(inventoryXmlPath)))
				inventoryXml.load(new FileInputStream(inventoryXmlPath));
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
		}

        List<GalaxyResource> inventoryResources = new ArrayList<>();

        for (String resource : inventoryXml.getInventory()) {
            GalaxyResource galaxyResource = getGalaxyResourceByName(resource);
            if (galaxyResource == null) {
                GalaxyResource retrievedGalaxyResource = retrieveGalaxyResource(resource);
                if (retrievedGalaxyResource != null)
                    inventoryResources.add(retrievedGalaxyResource);
            }
        }

        schematics.setAll(schematicsXml.getSchematics());
        inventory.setAll(inventoryResources);
	}

    public GalaxyResource retrieveGalaxyResource(String resource) {
        GalaxyResource galaxyResource = downloader.downloadGalaxyResource(resource);
        if (galaxyResource == null)
            return null;

        GalaxyResource existing = getGalaxyResourceByName(resource);
        if (existing != null) {
            resources.remove(existing);
        }

        resources.add(galaxyResource);
        return galaxyResource;
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

	public ObservableList<Schematic> getSchematics() {
		return schematics.get();
	}

	public ListProperty<Schematic> schematicsProperty() {
		return schematics;
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

	public ObjectProperty<Schematic> activeSchematicProperty() {
		return activeSchematic;
	}

	public BooleanProperty displayingAllGroupsProperty() {
		return displayingAllGroups;
	}
}
