package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HarvesterDroid {
	// TODO Status messages
	// TODO Move intensive methods to a Task

    private final static int DOWNLOAD_HOURS = 2;

	private final String schematicsXmlPath;
	private final String inventoryXmlPath;

	private final HarvesterDroidData data;

	private final Downloader downloader;

	private SchematicsXml schematicsXml;
	private InventoryXml inventoryXml;

	private ListProperty<GalaxyResource> inventory;
	private SimpleListProperty<GalaxyResource> resources;
	private ListProperty<Schematic> schematics;

	private FilteredList<GalaxyResource> filteredInventory;
	private FilteredList<GalaxyResource> filteredResources;

	private ObjectProperty<Schematic> activeSchematic;
    private StringProperty currentResourceTimestamp;

    public HarvesterDroid(String schematicsXmlPath, String inventoryXmlPath, Downloader downloader) {
		this.schematicsXmlPath = schematicsXmlPath;
		this.inventoryXmlPath = inventoryXmlPath;
		this.downloader = downloader;
		this.currentResourceTimestamp = new SimpleStringProperty();
		this.data = new HarvesterDroidData();
		init(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		createListeners();
    }

	private void init(Collection<Schematic> schematics, Collection<GalaxyResource> resources, Collection<GalaxyResource> inventory) {
		this.inventory = new SimpleListProperty<>(FXCollections.observableArrayList(inventory));
		filteredInventory = new FilteredList<>(this.inventory.get(), galaxyResource -> true);
		this.resources = new SimpleListProperty<>(FXCollections.observableArrayList(resources));
		filteredResources = new FilteredList<>(this.resources.get(), galaxyResource -> false);
		this.schematics = new SimpleListProperty<>(FXCollections.observableArrayList(schematics));
		activeSchematic = new SimpleObjectProperty<>(null);
	}

	private void createListeners() {
		activeSchematic.addListener(this::onSchematicSelected);

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

	private void onSchematicSelected(ObservableValue<? extends Schematic> observable, Schematic oldValue, Schematic newValue) {
		if (newValue != null)
		    filterResourcesForSchematic(newValue);
		else filteredResources.setPredicate(galaxyResource -> false);
	}

	private void filterResourcesForSchematic(Schematic schematic) {
		if (schematic == null || schematic.isIncomplete() || resources.isEmpty()) {
		    filteredResources.setPredicate(param -> false);
			return;
		}

		List<GalaxyResource> bestResources = getBestResourcesList(schematic);
		filteredResources.setPredicate(bestResources::contains);
	}

	private List<GalaxyResource> getBestResourcesList(Schematic schematic) {
		List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
		schematic.getResources().forEach(id -> {
			List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
			if (matchedResources != null) {
				GalaxyResource bestResource = collectBestResourceForSchematic(schematic, matchedResources);
				if (bestResource != null && !bestResources.contains(bestResource))
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
		List<String> resourceGroups = data.getResourceGroups(id);
		Collection<GalaxyResource> galaxyResourceList = resources.get();
		if (resourceGroups != null) {
		    // ID that was entered is a group of resources
			List<GalaxyResource> master = new ArrayList<>();
			for (String group : resourceGroups) {
				master.addAll(galaxyResourceList.stream()
						.filter(galaxyResource -> galaxyResource.getResourceType().getId().startsWith(group)
								|| galaxyResource.getResourceType().getId().equals(group))
						.collect(Collectors.toList()));
			}
			return master;
		} else {
			return galaxyResourceList.stream().filter(galaxyResource ->
					galaxyResource.getResourceType().getId().equals(id) || galaxyResource.getResourceType().getId().startsWith(id)
			).collect(Collectors.toList());
		}
	}

	public GalaxyResource getGalaxyResourceByName(String name) {
        Optional<GalaxyResource> optional = resources.get().stream().filter(galaxyResource -> galaxyResource.getName().equals(name)).findFirst();
        return optional.orElse(null);
	}

	private boolean needsUpdate(Date timestamp) {
		if (timestamp == null)
			return true;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
		LocalDateTime plusHours = from.plusHours(DOWNLOAD_HOURS);
		return now.isAfter(plusHours);
	}

	public void save() throws IOException, TransformerException {
		schematicsXml.setSchematics(getSchematics());
		inventoryXml.setInventory(getInventory().stream().map(GalaxyResource::getName).collect(Collectors.toList()));

		schematicsXml.save(new File(schematicsXmlPath));
		inventoryXml.save(new File(inventoryXmlPath));
	}

	public void updateResources() {
		try {
		    if (!needsUpdate(downloader.getCurrentResourcesTimestamp())) {
		        if (downloader.getCurrentResourcesTimestamp().toString().equals(getCurrentResourceTimestamp())) {
		            currentResourceTimestamp.set(downloader.getCurrentResourcesTimestamp().toString());
                }
            }

			downloader.downloadCurrentResources();
            for (GalaxyResource downloadedResource : downloader.getCurrentResources()) {
                populateResourceFromType(downloadedResource);
            }

            resources.clear();
			resources.addAll(downloader.getCurrentResources());
			currentResourceTimestamp.set(downloader.getCurrentResourcesTimestamp().toString());
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
            if (galaxyResource == null)
                galaxyResource = retrieveGalaxyResource(resource);
            if (galaxyResource != null)
                inventoryResources.add(galaxyResource);
        }

        schematics.setAll(schematicsXml.getSchematics());
        inventory.setAll(inventoryResources);
	}

    public GalaxyResource retrieveGalaxyResource(String resource) {
        GalaxyResource existing = getGalaxyResourceByName(resource);
        if (existing != null) {
            return existing;
        }

        GalaxyResource galaxyResource = downloader.downloadGalaxyResource(resource);
        if (galaxyResource == null)
            return null;

        populateResourceFromType(galaxyResource);
        resources.add(galaxyResource);
        return galaxyResource;
    }

    private void populateResourceFromType(GalaxyResource galaxyResource) {
        ResourceType type = data.getResourceTypeMap().get(galaxyResource.getResourceTypeString());
        if (type == null) {
            System.out.println("No resource type " + galaxyResource.getResourceTypeString());
            return;
        }
        galaxyResource.setResourceType(type);
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

	public FilteredList<GalaxyResource> getFilteredInventory() {
		return filteredInventory;
	}

	public FilteredList<GalaxyResource> getFilteredResources() {
		return filteredResources;
	}

	public ObjectProperty<Schematic> activeSchematicProperty() {
		return activeSchematic;
	}

    public String getCurrentResourceTimestamp() {
        return currentResourceTimestamp.get();
    }

    public ReadOnlyStringProperty currentResourceTimestampProperty() {
        return currentResourceTimestamp;
    }

    public Map<String, String> getResourceTypes() {
        Map<String, String> types = new HashMap<>();
        data.getResourceTypeMap().forEach((key, value) -> types.put(key, value.getName()));
        return types;
    }
}
