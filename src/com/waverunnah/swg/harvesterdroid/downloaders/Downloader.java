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

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.utils.Watcher;

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
    private final String baseUrl;
    private final String identifier;

    private final Map<String, GalaxyResource> currentResources = new HashMap<>();

    protected Downloader(String identifier, String baseUrl) {
        this.identifier = identifier;
        this.baseUrl = baseUrl;
        init();
    }

    private void init() {
        File currentResources = new File(getRootDownloadsPath() + "current_resources.dl");
        if (currentResources.exists()) {
            try {
                parseCurrentResources(new FileInputStream(currentResources));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void parseCurrentResources(InputStream currentResourcesStream) throws IOException;

    protected abstract GalaxyResource parseGalaxyResource(InputStream galaxyResourceStream);

    protected abstract InputStream getCurrentResourcesStream() throws IOException;

    protected abstract InputStream getGalaxyResourceStream(String resource) throws IOException;

    public abstract Date getCurrentResourcesTimestamp();

    public final DownloadResult downloadCurrentResources() throws IOException {
        InputStream in = null;

        File file = new File(getRootDownloadsPath() + "current_resources.dl");
        if (!file.exists() && !file.mkdirs())
            return DownloadResult.FAILED;

        try {
            in = getCurrentResourcesStream();

            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

            // Just in-case the user messes with something, we can re-download the XML
            Watcher.createFileWatcher(new File(getRootDownloadsPath() + "current_resources.dl"), () -> {
                try {
                    downloadCurrentResources();
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

        return DownloadResult.SUCCESS;
    }

    public final GalaxyResource downloadGalaxyResource(String resource) {
        InputStream in;

        File file = new File(getRootDownloadsPath() + resource + ".dl");
        if (!file.exists())
            file.mkdirs();

        try {
            in = getGalaxyResourceStream(resource);

            Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

            return parseGalaxyResource(new FileInputStream(file));
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

    public Collection<GalaxyResource> getCurrentResources() {
        return currentResources.values();
    }

    private String getRootDownloadsPath() {
        return Launcher.ROOT_DIR + "/" + getIdentifier() + "/";
    }

    public enum DownloadResult {
        FAILED,
        NO_ACTION, SUCCESS
    }

}
