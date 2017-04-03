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

package com.waverunnah.swg.harvesterdroid.downloaders;

import com.waverunnah.swg.harvesterdroid.app.Watcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.ui.dialog.ExceptionDialog;

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
import java.util.Map;

/**
 * Abstract class used by HarvesterDroid in the downloading of required data to function
 * <p>
 * This base class should have no knowledge of "how" the data is stored, only that it can be downloaded
 * from some location and turned into something usable for HarvesterDroid. The responsibility lies on
 * sub-classes to know how the data is stored and convert them to the resources map.
 */
public abstract class Downloader {
    private String galaxy;
    private final String root;
    private final String baseUrl;
    private final String identifier;

    private final Map<String, GalaxyResource> currentResources = new HashMap<>();

    protected Downloader(String root, String identifier, String baseUrl, String galaxy) {
        this.root = root;
        this.identifier = identifier;
        this.baseUrl = baseUrl;
        this.galaxy = galaxy;
        init();
    }

    private void init() {
        File currentResources = new File(getRootDownloadsPath() + "current_resources" + getGalaxy() + ".dl");
        if (currentResources.exists()) {
            try {
                parseCurrentResources(new FileInputStream(currentResources));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void parseCurrentResources(InputStream currentResourcesStream) throws IOException;

    protected abstract Map<String,String> parseGalaxyList(InputStream galaxyListStream);

    protected abstract GalaxyResource parseGalaxyResource(InputStream galaxyResourceStream);

    protected abstract InputStream getCurrentResourcesStream() throws IOException;

    protected abstract InputStream getGalaxyResourceStream(String resource) throws IOException;

    protected abstract InputStream getGalaxyListStream() throws IOException;

    public abstract Date getCurrentResourcesTimestamp();

    public final Map<String, String> downloadGalaxyList() {
        InputStream in;

        File file = new File(getRootDownloadsPath() + "servers.dl");
        if (!file.exists() && !file.mkdirs())
            return null;

        try {
            in = getGalaxyListStream();

            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

            return parseGalaxyList(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public final DownloadResult downloadCurrentResources(Map<String, ResourceType> resourceTypeMap) throws IOException {
        InputStream in = null;

        File file = new File(getRootDownloadsPath() + "current_resources" + getGalaxy() + ".dl");
        if (!file.exists() && !file.mkdirs())
            return DownloadResult.FAILED;

        try {
            in = getCurrentResourcesStream();

            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

            // Just in-case the user messes with something, we can re-download the XML
            Watcher.createFileWatcher(new File(getRootDownloadsPath() + "current_resources" + getGalaxy() + ".dl"), () -> {
                try {
                    downloadCurrentResources(resourceTypeMap);
                } catch (IOException e) {
                    ExceptionDialog.display(e);
                }
            });
        } catch (ConnectException e) {
            return DownloadResult.FAILED;
        } finally {
            if (in != null) {
                in.close();
            }
        }

        if (!file.exists())
            return DownloadResult.FAILED;

        parseCurrentResources(new FileInputStream(file));
        currentResources.values().forEach(galaxyResource -> populateResourceFromType(galaxyResource, resourceTypeMap));

        return DownloadResult.SUCCESS;
    }

    private void populateResourceFromType(GalaxyResource galaxyResource, Map<String, ResourceType> resourceTypeMap) {
        ResourceType type = resourceTypeMap.get(galaxyResource.getResourceTypeString());
        if (type == null) {
            System.out.println("No resource type " + galaxyResource.getResourceTypeString());
            return;
        }
        galaxyResource.setResourceType(type);
    }

    public final GalaxyResource downloadGalaxyResource(String resource, Map<String, ResourceType> resourceTypeMap) {
        InputStream in;

        File file = new File(getRootDownloadsPath() + resource + ".dl");
        if (!file.exists())
            file.mkdirs();

        try {
            in = getGalaxyResourceStream(resource);

            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

            GalaxyResource galaxyResource = parseGalaxyResource(new FileInputStream(file));
            if (galaxyResource != null)
                populateResourceFromType(galaxyResource, resourceTypeMap);
            return galaxyResource;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected final void populateCurrentResourcesMap(Map<String, GalaxyResource> parsedCurrentResources) {
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

    public final String getGalaxy() {
        return galaxy;
    }

    public final void setGalaxy(String galaxy) {
        this.galaxy = galaxy;
    }

    public enum DownloadResult {
        FAILED,
        SUCCESS
    }

}
