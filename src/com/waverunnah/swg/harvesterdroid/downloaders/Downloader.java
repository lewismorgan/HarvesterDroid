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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class used by HarvesterDroid in the downloading of required data to function
 *
 * This base class should have no knowledge of "how" the data is stored, only that it can be downloaded
 * from some location and turned into something usable for HarvesterDroid. The responsibility lies on
 * sub-classes to know how the data is stored and convert them to the resources map.
 */
public abstract class Downloader {
	private final static int DOWNLOAD_HOURS = 2;
	private final String baseUrl;
	private final String identifier;

	private final Map<String, GalaxyResource> currentResources = new HashMap<>();

	protected Downloader(String identifier, String baseUrl) {
		this.identifier = identifier;
		this.baseUrl = baseUrl;
	}

	protected abstract void parseCurrentResources(InputStream currentResourcesStream) throws IOException;

	protected abstract InputStream getCurrentResourcesStream() throws IOException;
	public abstract Date getCurrentResourcesTimestamp();

	public final DownloadResult downloadCurrentResources() throws IOException {
		InputStream in = null;

		File file = new File(getRootDownloadsPath() + "current_resources.dl");
		// Don't download if you the timestamp is within 2 hours
		if (!needsUpdate(getCurrentResourcesTimestamp())) {
			return DownloadResult.NO_ACTION;
		} else {
			if (!file.exists() && !file.mkdirs()) {
				return DownloadResult.FAILED;
			}
		}

		try {
			in = getCurrentResourcesStream();

			Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

			// Just in-case the user messes with something, we can re-download the XML
			Watcher.createFileWatcher(new File(getRootDownloadsPath()+ "/current_resources.dl"), () -> {
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

	protected final void populateCurrentResourcesMap(Map<String, GalaxyResource> parsedCurrentResources) {
		currentResources.clear();
		currentResources.putAll(parsedCurrentResources);
	}

	protected final boolean needsUpdate(Date timestamp) {
		if (timestamp == null)
			return true;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
		LocalDateTime plusHours = from.plusHours(DOWNLOAD_HOURS);
		return now.isAfter(plusHours);
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

	public Map<String, GalaxyResource> getCurrentResourcesMap() {
		return currentResources;
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
