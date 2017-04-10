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
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.database.DatabaseManager;
import com.waverunnah.swg.harvesterdroid.downloaders.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.XmlFactory;
import com.waverunnah.swg.harvesterdroid.xml.app.InventoryXml;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HarvesterDroid {
    private final static int DOWNLOAD_HOURS = 2;

    private final HarvesterDroidData data;
    private final DatabaseManager databaseManager;


    private Downloader downloader;

    private List<InventoryResource> inventory;

    private List<GalaxyResource> resources;
    private List<Schematic> schematics;

    private Map<String, String> galaxies;

    private Map<String, String> themes;

    private long lastUpdateTimestamp;

    private long currentResourceTimestamp;
    private String activeGalaxy;
    private String activeTheme;

    public HarvesterDroid(Downloader downloader, DatabaseManager databaseManager) {
        this.downloader = downloader;
        this.databaseManager = databaseManager;
        this.currentResourceTimestamp = 0;
        this.data = new HarvesterDroidData();
        this.inventory = new ArrayList<>(0);
        this.resources = new ArrayList<>(0);
        this.schematics = new ArrayList<>(0);
        this.galaxies = new HashMap<>(0);
        this.themes = new HashMap<>();
    }

    public List<GalaxyResource> getBestResourcesList(Schematic schematic, boolean onlyAvailable) {
        List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
        schematic.getResources().forEach(id -> {
            List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
            if (matchedResources != null) {
                if (onlyAvailable)
                    matchedResources = matchedResources.stream()
                            .filter(resource -> resource.getDespawnDate() == null || inventoryContainsResource(resource))
                            .collect(Collectors.toList());

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
        List<String> resourceGroups = downloader.getResourceGroups(id);
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

    public GalaxyResource getGalaxyResource(String name) {
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

    public void updateResources() {
        try {
            if (downloader.getGalaxy().equals(activeGalaxy) && !needsUpdate(downloader.getCurrentResourcesTimestamp())) {
                if (downloader.getCurrentResourcesTimestamp().toString().equals(getCurrentResourceTimestamp())) {
                    currentResourceTimestamp = downloader.getCurrentResourcesTimestamp().getTime();
                }
            } else {
                galaxies = downloader.downloadGalaxyList();

                downloader.downloadCurrentResources();

                for (GalaxyResource currentResource : downloader.getCurrentResources()) {
                    // Remove any current loaded resources that are saved because they're probably is updated information
                    GalaxyResource duplicate = null;
                    for (GalaxyResource resource : resources) {
                        if (resource.getName().equals(currentResource.getName())) {
                            duplicate = resource;
                            break;
                        }
                    }
                    if (duplicate != null)
                        resources.remove(duplicate);
                }

                resources.addAll(downloader.getCurrentResources());

                resources.forEach(galaxyResource -> data.populateMinMax(galaxyResource.getResourceType()));
                inventory.forEach(this::getGalaxyResource);

                currentResourceTimestamp = downloader.getCurrentResourcesTimestamp().getTime();
                activeGalaxy = downloader.getGalaxy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GalaxyResource getGalaxyResource(InventoryResource inventoryResource) {
        if (!inventoryResource.getTracker().equals(getTracker()) || !inventoryResource.getGalaxy().equals(downloader.getGalaxy()))
            return null;

        GalaxyResource galaxyResource = getGalaxyResource(inventoryResource.getName());
        if (galaxyResource == null)
            galaxyResource = retrieveGalaxyResource(inventoryResource.getName());

        return galaxyResource;
    }

    public GalaxyResource retrieveGalaxyResource(String resource) {
        GalaxyResource existing = getGalaxyResource(resource);
        if (existing != null) {
            return existing;
        }

        GalaxyResource galaxyResource = downloader.downloadGalaxyResource(resource);
        if (galaxyResource == null)
            return null;

        data.populateMinMax(galaxyResource.getResourceType());
        resources.add(galaxyResource);
        return galaxyResource;
    }

    public boolean inventoryContainsResource(GalaxyResource galaxyResource) {
        for (InventoryResource inventoryResource : inventory) {
            if (galaxyResource.getName().equals(inventoryResource.getName()))
                return true;
        }
        return false;
    }

    public void switchToGalaxy(String galaxy) {
        if (this.activeGalaxy.equals(galaxy))
            return;

        activeGalaxy = galaxy;
        downloader.setGalaxy(galaxy);
        resources.clear();
        if (new File(getSavedResourcesPath() + ".mv.db").exists())
            loadResources(getSavedResourcesPath());
        updateResources();
    }

    public boolean addInventoryResource(GalaxyResource galaxyResource) {
        for (InventoryResource inventoryResource : inventory) {
            if (inventoryResource.getGalaxy().equals(downloader.getGalaxy()) && inventoryResource.getName().equals(galaxyResource.getName()))
                return false;
        }

        return inventory.add(new InventoryResource(galaxyResource.getName(), getTracker(), downloader.getGalaxy()));
    }

    public void removeInventoryResource(GalaxyResource galaxyResource) {
        InventoryResource toRemove = null;
        for (InventoryResource inventoryResource : inventory) {
            if (galaxyResource.getName().equals(inventoryResource.getName())) {
                toRemove = inventoryResource;
                break;
            }
        }

        if (toRemove != null)
            inventory.remove(toRemove);
    }

    public void saveSchematics(OutputStream outputStream) {
        SchematicsXml schematicsXml = new SchematicsXml();
        schematicsXml.setSchematics(schematics);
        XmlFactory.write(schematicsXml, outputStream);
    }

    public void saveInventory(OutputStream outputStream) {
        InventoryXml inventoryXml = new InventoryXml();
        inventoryXml.setInventory(inventory);
        XmlFactory.write(inventoryXml, outputStream);
    }

    public void saveResources() {
        EntityManager entityManager = databaseManager.createDatabase(getSavedResourcesPath());

        entityManager.getTransaction().begin();
        try {
            entityManager.createQuery("DELETE from GalaxyResource").executeUpdate();
            entityManager.createQuery("DELETE from ResourceType").executeUpdate();
        } catch (PersistenceException exc) {
            // db never existed so no need to clear
        }
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        resources.forEach(entityManager::persist);
        entityManager.getTransaction().commit();
    }

    public void shutdown() {
        databaseManager.shutdown();
    }

    public void loadResources(String database) {
        EntityManager entityManager = databaseManager.loadDatabase(database);
        resources = DatabaseManager.getList(entityManager, GalaxyResource.class);

        databaseManager.closeDatabase(database);
    }


    public void loadSchematics(InputStream inputStream) {
        SchematicsXml schematicsXml = XmlFactory.read(SchematicsXml.class, inputStream);
        if (schematicsXml != null && schematicsXml.getSchematics() != null)
            schematics = schematicsXml.getSchematics();
    }

    public void loadInventory(InputStream inputStream) {
        InventoryXml inventoryXml = XmlFactory.read(InventoryXml.class, inputStream);
        if (inventoryXml != null && inventoryXml.getInventory() != null) {
            inventory = inventoryXml.getInventory();
            inventory.forEach(this::getGalaxyResource);
        }
    }

    public List<InventoryResource> getInventory() {
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

    public long getCurrentResourceTimestamp() {
        return currentResourceTimestamp;
    }

    public Map<String, String> getGalaxies() {
        return galaxies;
    }

    public Map<String, String> getResourceTypes() {
        Map<String, String> types = new HashMap<>();
        downloader.getResourceTypeMap().forEach((key, value) -> types.put(key, value.getName()));
        return types;
    }

    public String getSavedResourcesPath() {
        return downloader.getResourcesPath();
    }

    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
    }

    public String getTracker() {
        return downloader.getIdentifier();
    }

    public String getActiveGalaxy() {
        return (galaxies.get(activeGalaxy) != null ? galaxies.get(activeGalaxy) : "No Active Galaxy");
    }

    public Map<String, String> getThemes() {
        return themes;
    }

    public void setThemes(Map<String, String> themes) {
        this.themes = themes;
    }

    public String getActiveTheme() {
        return activeTheme;
    }

    public void setActiveTheme(String activeTheme) {
        this.activeTheme = activeTheme;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }
}
