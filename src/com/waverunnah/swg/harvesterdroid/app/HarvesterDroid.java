/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.InventoryResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HarvesterDroid {
    // TODO Status messages
    // TODO Move intensive methods to a Task
    // TODO Refactor XML handling

    private final static int DOWNLOAD_HOURS = 2;

    private final String inventoryXmlPath;

    private final HarvesterDroidData data;

    private final Downloader downloader;

    private InventoryXml inventoryXml;

    private List<InventoryResource> inventoryResources;

    private List<GalaxyResource> inventory;
    private List<GalaxyResource> resources;
    private List<Schematic> schematics;

    private Map<String, String> galaxies;

    private String currentResourceTimestamp;
    private String tracker;


    public HarvesterDroid(String inventoryXmlPath, String tracker, Downloader downloader) {
        this.inventoryXmlPath = inventoryXmlPath;
        this.downloader = downloader;
        this.tracker = tracker;
        this.inventoryResources = new ArrayList<>();
        this.currentResourceTimestamp = "";
        this.data = new HarvesterDroidData();
        this.inventory = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.schematics = new ArrayList<>();
    }

    public List<GalaxyResource> getBestResourcesList(Schematic schematic) {
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
        Map<String, Integer> modifiers = schematic.getModifiers();

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

    public float getResourceWeightedAverage(Map<String, Integer> modifiers, GalaxyResource resource) {
        float average = 0;

        for (Map.Entry<String, Integer> modifier : modifiers.entrySet()) {
            float value = resource.getAttribute(modifier.getKey());
            if (value == -1)
                continue;

            average += (value * (float) modifier.getValue() / 100);
        }

        average = average / 1000;
        return average;
    }

    public List<GalaxyResource> findGalaxyResourcesById(String id) {
        List<String> resourceGroups = data.getResourceGroups(id);
        if (resourceGroups != null) {
            // ID that was entered is a group of resources
            List<GalaxyResource> master = new ArrayList<>();
            for (String group : resourceGroups) {
                master.addAll(resources.stream()
                        .filter(galaxyResource -> galaxyResource.getResourceType().getId().startsWith(group)
                                || galaxyResource.getResourceType().getId().equals(group))
                        .collect(Collectors.toList()));
            }
            return master;
        } else {
            return resources.stream().filter(galaxyResource ->
                    galaxyResource.getResourceType().getId().equals(id) || galaxyResource.getResourceType().getId().startsWith(id)
            ).collect(Collectors.toList());
        }
    }

    public GalaxyResource getGalaxyResourceByName(String name) {
        Optional<GalaxyResource> optional = resources.stream().filter(galaxyResource -> galaxyResource.getName().equals(name)).findFirst();
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

    public void save() {
        //schematicsXml.setSchematics(getSchematics());
        inventoryXml.setInventory(inventoryResources);

        try {
            //schematicsXml.save(new File(schematicsXmlPath));
            inventoryXml.save(new File(inventoryXmlPath));
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {

    }

    public void updateResources() {
        try {
            if (!needsUpdate(downloader.getCurrentResourcesTimestamp())) {
                if (downloader.getCurrentResourcesTimestamp().toString().equals(getCurrentResourceTimestamp())) {
                    currentResourceTimestamp = downloader.getCurrentResourcesTimestamp().toString();
                }
            }

            galaxies = downloader.downloadGalaxyList();

            downloader.downloadCurrentResources();
            for (GalaxyResource downloadedResource : downloader.getCurrentResources()) {
                populateResourceFromType(downloadedResource);
            }

            resources.clear();
            resources.addAll(downloader.getCurrentResources());
            currentResourceTimestamp = downloader.getCurrentResourcesTimestamp().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSavedData() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            inventoryXml = new InventoryXml(factory.newDocumentBuilder());

            if (Files.exists(Paths.get(inventoryXmlPath)))
                inventoryXml.load(new FileInputStream(inventoryXmlPath));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        if (inventoryXml != null) {
            inventoryResources = inventoryXml.getInventory();
            refreshInventoryResources();
        }
    }

    public void refreshInventoryResources() {
        List<GalaxyResource> inventory = new ArrayList<>();

        String galaxy = downloader.getGalaxy();
        for (InventoryResource inventoryResource : inventoryResources) {
            if (!Objects.equals(inventoryResource.getTracker(), tracker) && !Objects.equals(inventoryResource.getGalaxy(), galaxy))
                return;

            GalaxyResource galaxyResource = getGalaxyResourceByName(inventoryResource.getName());
            if (galaxyResource == null)
                galaxyResource = retrieveGalaxyResource(inventoryResource.getName());
            if (galaxyResource != null)
                inventory.add(galaxyResource);
        }

        this.inventory = inventory;
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

    public void switchToGalaxy(String galaxy) {
        if (Objects.equals(galaxy, downloader.getGalaxy()))
            return;

        downloader.setGalaxy(galaxy);
        updateResources();
        refreshInventoryResources();
    }

    public void addInventoryResource(GalaxyResource galaxyResource) {
        boolean exists = false;
        for (InventoryResource inventoryResource : inventoryResources) {
            if (inventoryResource.getName().equals(galaxyResource.getName())) {
                exists = true;
                break;
            }
        }
        if (!exists)
            inventoryResources.add(new InventoryResource(galaxyResource.getName(), tracker, downloader.getGalaxy()));
    }

    private void populateResourceFromType(GalaxyResource galaxyResource) {
        ResourceType type = data.getResourceTypeMap().get(galaxyResource.getResourceTypeString());
        if (type == null) {
            System.out.println("No resource type " + galaxyResource.getResourceTypeString());
            return;
        }
        galaxyResource.setResourceType(type);
    }

    public List<GalaxyResource> getInventory() {
        return inventory;
    }

    public List<GalaxyResource> getResources() {
        return resources;
    }

    public List<Schematic> getSchematics() {
        return schematics;
    }

    public void setSchematics(List<Schematic> schematics) {
        this.schematics = schematics;
    }

    public String getCurrentResourceTimestamp() {
        return currentResourceTimestamp;
    }

    public Map<String, String> getGalaxies() {
        return galaxies;
    }

    public Map<String, String> getResourceTypes() {
        Map<String, String> types = new HashMap<>();
        data.getResourceTypeMap().forEach((key, value) -> types.put(key, value.getName()));
        return types;
    }
}
