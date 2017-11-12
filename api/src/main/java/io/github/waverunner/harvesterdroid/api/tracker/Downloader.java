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

package io.github.waverunner.harvesterdroid.api.tracker;

import io.github.waverunner.harvesterdroid.api.resource.GalaxyResource;
import io.github.waverunner.harvesterdroid.api.resource.ResourceType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class used by HarvesterDroid in the downloading of required data to function.
 *
 * <p>This base class should have no knowledge of "how" the data is stored, only that it can be
 * downloaded from some location and turned into something usable for HarvesterDroid. The
 * responsibility lies on sub-classes to know how the data is stored and convert them to the
 * resources map.
 */
public abstract class Downloader {

  private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

  private final String root;
  private final String baseUrl;
  private final String identifier;
  private final Map<String, GalaxyResource> currentResources = new HashMap<>();
  private String galaxy;
  private Map<String, ResourceType> resourceTypeMap = new HashMap<>();
  private Map<String, List<String>> resourceGroups = new HashMap<>();

  protected Downloader(String root, String identifier, String baseUrl, String galaxy) {
    this.root = root;
    this.identifier = identifier;
    this.baseUrl = baseUrl;
    this.galaxy = galaxy;
    init();
  }

  private void init() {
    try {
      downloadResourceTypes(resourceTypeMap, resourceGroups);
    } catch (IOException e) {
      e.printStackTrace();
    }

    File currentResources = new File(
        getRootDownloadsPath() + "current_resources" + getGalaxy() + ".dl");
    if (currentResources.exists()) {
      try (FileInputStream fileInputStream = new FileInputStream(currentResources)) {
        parseCurrentResources(fileInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  protected abstract void parseCurrentResources(InputStream currentResourcesStream)
      throws IOException;

  protected abstract Map<String, String> parseGalaxyList(InputStream galaxyListStream);

  protected abstract GalaxyResource parseGalaxyResource(InputStream galaxyResourceStream);

  protected abstract InputStream getCurrentResourcesStream() throws IOException;

  protected abstract InputStream getGalaxyResourceStream(String resource) throws IOException;

  protected abstract InputStream createGalaxyListStream() throws IOException;

  public abstract Date getCurrentResourcesTimestamp();

  public final Map<String, String> downloadGalaxyList() {
    File file = new File(getRootDownloadsPath() + "servers.dl");
    if (!file.exists() && !file.mkdirs()) {
      return null;
    }

    try (InputStream inputStream = createGalaxyListStream()) {
      Files.copy(inputStream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
      return parseGalaxyList(new FileInputStream(file));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  protected abstract void downloadResourceTypes(Map<String, ResourceType> resourceTypeMap,
      Map<String, List<String>> resourceGroups) throws IOException;

  public final DownloadResult downloadCurrentResources() throws IOException {

    File file = new File(getRootDownloadsPath() + "current_resources_" + getGalaxy() + ".dl");
    if (!file.exists() && !file.mkdirs()) {
      return DownloadResult.FAILED;
    }

    try (InputStream in = getCurrentResourcesStream()) {
      Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
    } catch (ConnectException e) {
      return DownloadResult.FAILED;
    }

    if (!file.exists()) {
      return DownloadResult.FAILED;
    }

    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      parseCurrentResources(fileInputStream);
    }

    currentResources.values().forEach(this::populateResourceFromType);

    return DownloadResult.SUCCESS;
  }

  private void populateResourceFromType(GalaxyResource galaxyResource) {
    ResourceType type = resourceTypeMap.get(galaxyResource.getResourceTypeString());
    if (type == null) {
      logger.warn("No resource type exists called {} ", galaxyResource.getResourceTypeString());
      return;
    }
    galaxyResource.setResourceType(type);
  }

  public final GalaxyResource downloadGalaxyResource(String resource) {
    try {
      GalaxyResource galaxyResource = parseGalaxyResource(getGalaxyResourceStream(resource));
      if (galaxyResource != null) {
        populateResourceFromType(galaxyResource);
      }
      return galaxyResource;
    } catch (IOException e) {
      throw new RuntimeException("Error downloading resource " + resource);
    }
  }

  protected final void populateCurrentResourcesMap(
      Map<String, GalaxyResource> parsedCurrentResources) {
    currentResources.clear();
    currentResources.putAll(parsedCurrentResources);
  }

  public final InputStream getInputStreamFromUrl(String url) throws IOException {
    return new URL(getBaseUrl() + url).openStream();
  }

  public final String getBaseUrl() {
    return baseUrl;
  }

  public final String getIdentifier() {
    return identifier;
  }

  public final Collection<GalaxyResource> getCurrentResources() {
    return currentResources.values();
  }

  private String getRootDownloadsPath() {
    return root + "/" + getIdentifier() + "/";
  }

  public final String getResourcesPath() {
    return getRootDownloadsPath() + "resources_" + getGalaxy() + ".bson";
  }

  public final String getGalaxy() {
    return galaxy;
  }

  public final void setGalaxy(String galaxy) {
    this.galaxy = galaxy;
  }

  public List<String> getResourceGroups(String group) {
    return resourceGroups.get(group);
  }

  public Map<String, ResourceType> getResourceTypeMap() {
    return resourceTypeMap;
  }

  public enum DownloadResult {
    FAILED,
    SUCCESS
  }

}
