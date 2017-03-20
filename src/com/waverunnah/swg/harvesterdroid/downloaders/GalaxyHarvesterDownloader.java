package com.waverunnah.swg.harvesterdroid.downloaders;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterCurrentResourcesXml;
import com.waverunnah.swg.harvesterdroid.xml.galaxyharvester.HarvesterResourceXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    protected GalaxyResource parseGalaxyResource(InputStream galaxyResourceStream) {
        try {
            HarvesterResourceXml resourceXml = new HarvesterResourceXml(xmlFactory.newDocumentBuilder());
            resourceXml.load(galaxyResourceStream);
            return resourceXml.getGalaxyResource();
        } catch (ParserConfigurationException | SAXException | IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    @Override
	public InputStream getCurrentResourcesStream() throws IOException {
		return getInputStreamFromUrl("exports/current48.xml");
	}

    @Override
    protected InputStream getGalaxyResourceStream(String resource) throws IOException {
        return getInputStreamFromUrl("getResourceByName.py?name=" + resource + "&galaxy=48");
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

	public InputStream download(String url) throws IOException {
		return new URL("http://galaxyharvester.net/" + url).openStream();
	}
}
