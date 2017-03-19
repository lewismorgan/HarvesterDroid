package com.waverunnah.swg.harvesterdroid.utils;

import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;
import com.waverunnah.swg.harvesterdroid.xml.app.ResourceXml;
import com.waverunnah.swg.harvesterdroid.xml.galacticharvester.HarvesterResourceXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Downloader {
	// TODO: Refactor for downloading from other locations

	private static Map<String, List<String>> resourceGroups = new HashMap<>();
	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

	public static File downloadCurrentResources() throws IOException {
		URL website;
		InputStream in = null;

		File file = new File(Launcher.ROOT_DIR + "/current_resources.dl");
		try {
			website = new URL("http://galaxyharvester.net/exports/current48.xml");
			in = website.openStream();

			Files.copy(in, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);

			// Just in-case the user messes with something, we can re-download the XML
			Watcher.createFileWatcher(new File(Launcher.ROOT_DIR + "/current_resources.dl"), () -> {
				try {
					downloadCurrentResources();
				} catch (IOException e) {
					ExceptionDialog.display(e);
				}
			});

		} catch (ConnectException e) {
			System.out.println("Connection failed.");
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return file;
	}

	public static GalaxyResource downloadGalaxyResource(String name) throws IOException {
		File dir = new File(Launcher.ROOT_DIR + "/downloaded_resources");
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(Launcher.ROOT_DIR + "/downloaded_resources/" + name + ".dl");

		InputStream stream = null;
		try {
			ResourceXml resourceXml = new HarvesterResourceXml(documentBuilderFactory.newDocumentBuilder());

			if (!file.exists()) {
				stream = downloadFile("getResourceByName.py?name=" + name + "&galaxy=48");
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

	public static InputStream downloadFile(String url) throws IOException {
		return new URL("http://galaxyharvester.net/" + url).openStream();
	}

	public static List<String> getResourceGroups(String group) {
		return resourceGroups.get(group);
	}

	public static void printInputStream(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}
		System.out.println(out.toString());   //Prints the string content read from input stream
		reader.close();
	}

	// TODO Remove static initialization
	static {
		// This only needs to be done for the resources that do not follow the proper hierarchy naming convention
		try {
			CodeSource src = Launcher.class.getProtectionDomain().getCodeSource();
			String path = "com/waverunnah/swg/harvesterdroid/data/raw/groups/";
			if (src != null) {
				ZipInputStream zip = new ZipInputStream(src.getLocation().openStream());
				ZipEntry entry = zip.getNextEntry();

				if (entry == null) {
					File dir = new File(src.getLocation().getPath() + path);
					File[] files = dir.listFiles();
					for (File file : files != null ? files : new File[0]) {
						populateResourceGroup(file.getAbsolutePath());
					}
				} else {
					while (entry != null) {
						if (entry.getName() != null)
							populateResourceGroup(entry.getName());
						entry = zip.getNextEntry();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void populateResourceGroup(String file) {
		List<String> resourceGroup = new ArrayList<>();
		resourceGroup.add(file.substring(file.lastIndexOf("\\") + 1));
		try {
			try (Stream<String> stream = Files.lines(Paths.get(file))) {
				stream.forEach(resourceGroup::add);
			}
		} catch (IOException e) {
			ExceptionDialog.display(e);
		}
		resourceGroups.put(file.substring(file.lastIndexOf("\\") + 1), resourceGroup);
	}
}
