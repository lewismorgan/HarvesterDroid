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

package com.lewisjmorgan.harvesterdroid.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.lewisjmorgan.harvesterdroid.api.DataFactory;
import com.lewisjmorgan.harvesterdroid.api.Downloader;
import com.lewisjmorgan.harvesterdroid.api.GalaxyResource;
import com.lewisjmorgan.harvesterdroid.data.resources.InventoryResource;
import com.lewisjmorgan.harvesterdroid.data.schematics.Schematic;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HarvesterDroid {
  private static final Logger logger = LogManager.getLogger(HarvesterDroid.class);

  private final HarvesterDroidData data;

  private Downloader downloader;

  private final Set<InventoryResource> inventory;
  private final Set<GalaxyResource> resources;
  private final List<Schematic> schematics;

  private Map<String, String> galaxies;
  private Map<String, String> themes;

  private long lastUpdateTimestamp;
  private long updateTimestamp;

  private String activeGalaxy;
  private String activeTheme;

  public HarvesterDroid(Downloader downloader) {
    this.downloader = downloader;
    this.data = new HarvesterDroidData();
    this.inventory = Collections.synchronizedSet(new HashSet<>(0));
    this.resources = Collections.synchronizedSet(new HashSet<>(0));
    this.schematics = new ArrayList<>(0);
    this.galaxies = new HashMap<>(0);
    this.themes = new HashMap<>();
  }

  public List<GalaxyResource> getBestResourcesList(Schematic schematic, boolean onlyAvailable) {
    List<GalaxyResource> bestResources = new ArrayList<>(schematic.getResources().size());
    schematic.getResources().forEach(id -> {
      List<GalaxyResource> matchedResources = findGalaxyResourcesById(id);
      if (matchedResources != null) {
        if (onlyAvailable) {
          matchedResources = matchedResources.stream()
              .filter(resource -> resource.getDespawnDate() == null || inventoryContainsResource(resource))
              .collect(Collectors.toList());
        }

        GalaxyResource bestResource = getBestResource(schematic, matchedResources);
        if (bestResource != null && !bestResources.contains(bestResource)) {
          bestResources.add(bestResource);
        }
      }
    });
    return bestResources;
  }

  public GalaxyResource getBestResource(Schematic schematic, List<GalaxyResource> galaxyResources) {
    GalaxyResource ret = null;
    float weightedAvg = -1;
    Map<String, Integer> modifiers = schematic.getModifiers();

    for (GalaxyResource galaxyResource : galaxyResources) {
      float galaxyResourceAvg = calculateResourceWeightedAverage(modifiers, galaxyResource);
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

  private float calculateResourceWeightedAverage(Map<String, Integer> modifiers, GalaxyResource resource) {
    float average = 0;

    for (Map.Entry<String, Integer> modifier : modifiers.entrySet()) {
      float value = resource.getAttribute(modifier.getKey());
      if (value == -1) {
        continue;
      }

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

  public void refreshResources(boolean loadLocal) {
    try {
      if (downloader != null) {
        galaxies = downloader.downloadGalaxyList();
        activeGalaxy = downloader.getGalaxy();

        if (loadLocal && Files.exists(Paths.get(downloader.getResourcesPath()))) {
          loadResources(Files.readAllBytes(Paths.get(downloader.getResourcesPath())));
        }

        downloadNewResources();

        resources.forEach(galaxyResource -> data.populateMinMax(galaxyResource.getResourceType()));
        logger.debug("Refreshed resources. There are {} resources now loaded", resources.size());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void downloadNewResources() throws IOException {
    downloader.downloadCurrentResources();

    List<GalaxyResource> downloaded = new ArrayList<>(downloader.getCurrentResources());

    List<String> filtered = downloaded.stream()
        .filter(dlResource -> resources.stream().anyMatch(resource -> resource.getName().equals(dlResource.getName())))
        .map(GalaxyResource::getName).collect(Collectors.toList());

    resources.removeIf(resource -> filtered.contains(resource.getName()));
    logger.debug("Removed {} resources from resources as they're still active. Resources size "
        + "is now {} resources", filtered.size(), resources.size());

    resources.addAll(downloaded);

    logger.debug("Finished downloading {} resources to listing of resources (now {} resources)",
        downloaded.size(), resources.size());
  }

  public GalaxyResource getGalaxyResource(String name) {
    Optional<GalaxyResource> optional = resources.stream().filter(galaxyResource -> galaxyResource.getName().equals(name)).findFirst();
    return optional.orElse(null);
  }

  public GalaxyResource getGalaxyResource(InventoryResource inventoryResource) {
    GalaxyResource galaxyResource = getGalaxyResource(inventoryResource.getName());
    if (galaxyResource == null) {
      galaxyResource = findGalaxyResource(inventoryResource.getName());
    }

    return galaxyResource;
  }

  public GalaxyResource findGalaxyResource(String resource) {
    GalaxyResource existing = getGalaxyResource(resource);
    if (existing != null) {
      return existing;
    }

    GalaxyResource galaxyResource = downloader.downloadGalaxyResource(resource);
    if (galaxyResource == null) {
      return null;
    }

    data.populateMinMax(galaxyResource.getResourceType());
    resources.add(galaxyResource);
    return galaxyResource;
  }

  public boolean inventoryContainsResource(GalaxyResource galaxyResource) {
    for (InventoryResource inventoryResource : inventory) {
      if (galaxyResource.getName().equals(inventoryResource.getName())) {
        return true;
      }
    }
    return false;
  }

  public boolean switchToGalaxy(String galaxy) {
    if (this.activeGalaxy.equals(galaxy)) {
      return false;
    }

    try (FileOutputStream fileOutputStream = new FileOutputStream(getSavedResourcesPath())) {
      saveResources(fileOutputStream);
    } catch (IOException e) {
      logger.error("Failed to save resources when switching galaxies", e);
    }

    activeGalaxy = galaxy;
    downloader.setGalaxy(galaxy);

    refreshResources(true);
    return true;
  }

  public boolean addInventoryResource(GalaxyResource galaxyResource) {
    for (InventoryResource inventoryResource : inventory) {
      if (inventoryResource.getGalaxy().equals(downloader.getGalaxy()) && inventoryResource.getName().equals(galaxyResource.getName())) {
        return false;
      }
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

    if (toRemove != null) {
      inventory.remove(toRemove);
    }
  }

  public void saveSchematics(OutputStream outputStream) {
    ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    try {
      objectMapper.writeValue(outputStream, schematics);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void saveInventory(OutputStream outputStream) {
    ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    try {
      objectMapper.writeValue(outputStream, inventory);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void saveResources(OutputStream outputStream) throws IOException {
    DataFactory.save(outputStream, resources);
    outputStream.close();
  }

  public void loadResources(byte[] data) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
    HashSet<GalaxyResource> saved = DataFactory.openBinaryCollection(byteArrayInputStream,
        new TypeReference<HashSet<GalaxyResource>>() {});

    if (saved != null) {
      resources.clear();
      resources.addAll(saved);
    }

    byteArrayInputStream.close();
    logger.debug("Loaded {} resources from data input stream", resources.size());
  }

  public void loadSchematics(InputStream inputStream) throws IOException {
    ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
    List<Schematic> saved = objectMapper.readValue(inputStream, new TypeReference<List<Schematic>>() {
    });

    schematics.clear();
    schematics.addAll(saved);

    inputStream.close();
  }

  public void loadInventory(InputStream inputStream) throws IOException {
    ObjectMapper objectMapper = DataFactory.createJsonObjectMapper();
    Set<InventoryResource> saved = objectMapper.readValue(inputStream, new TypeReference<Set<InventoryResource>>() {
    });

    inventory.clear();
    inventory.addAll(saved);

    inputStream.close();
  }

  public Set<InventoryResource> getInventory() {
    return inventory;
  }

  public Set<GalaxyResource> getResources() {
    return resources;
  }

  public List<Schematic> getSchematics() {
    return schematics;
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

  public long getCurrentUpdateTimestamp() {
    return downloader.getCurrentResourcesTimestamp().getTime();
  }
}
