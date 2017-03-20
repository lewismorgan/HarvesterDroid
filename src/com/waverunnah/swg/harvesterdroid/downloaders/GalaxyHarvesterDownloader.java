package com.waverunnah.swg.harvesterdroid.downloaders;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.xml.app.ResourceXml;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterCurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterResourceXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.waverunnah.swg.harvesterdroid.Launcher.ROOT_DIR;

public final class GalaxyHarvesterDownloader extends Downloader {
	private DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
	private HarvesterCurrentResourcesXml currentResourcesXml;

	public GalaxyHarvesterDownloader() {
		super("galaxyharvester", "https://galaxyharvester.net/");
	}

	@Override
	protected void parseCurrentResources(InputStream currentResourcesStream) throws IOException {
		try {
			currentResourcesXml = new HarvesterCurrentResourcesXml(xmlFactory.newDocumentBuilder());
			currentResourcesXml.load(currentResourcesStream);

			populateCurrentResourcesMap(currentResourcesXml.getGalaxyResources());
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getCurrentResourcesStream() throws IOException {
		return getInputStreamFromUrl("exports/current48.xml");
	}

	@Override
	public Date getCurrentResourcesTimestamp() {
		if (currentResourcesXml == null || currentResourcesXml.getTimestamp() == null
				|| currentResourcesXml.getTimestamp().isEmpty())
			return null;
		DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z");
		try {
			return dateFormat.parse(currentResourcesXml.getTimestamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// TODO Refactor into generic Downloader class
	public GalaxyResource downloadGalaxyResource(String name) throws IOException {
		File dir = new File(ROOT_DIR + "/downloaded_resources");
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(ROOT_DIR + "/downloaded_resources/" + name + ".dl");

		InputStream stream = null;
		try {
			ResourceXml resourceXml = new HarvesterResourceXml(xmlFactory.newDocumentBuilder());

			if (!file.exists()) {
				stream = download("getResourceByName.py?name=" + name + "&galaxy=48");
				Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
				stream.close();
			}

			stream = new FileInputStream(file);

			resourceXml.load(stream);

			if (resourceXml.getGalaxyResource() == null)
				file.delete();
			return resourceXml.getGalaxyResource();
		} catch (ParserConfigurationException | SAXException e) {
			ExceptionDialog.display(e);
		} finally {
			if (stream != null)
				stream.close();
		}
		return null;
	}

	public InputStream download(String url) throws IOException {
		return new URL("http://galaxyharvester.net/" + url).openStream();
	}
}
