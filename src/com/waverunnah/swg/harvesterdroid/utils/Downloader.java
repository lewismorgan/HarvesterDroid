package com.waverunnah.swg.harvesterdroid.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Downloader {
	// TODO: Refactor to allow downloading of different xml files

	private static Map<String, List<String>> resourceGroups = new HashMap<>();

	public static File downloadXmls() throws IOException {
		URL website;
		InputStream in = null;

		if (Files.exists(Paths.get("data/current48.xml")))
			return new File("data/current48.xml");
		try {
			website = new URL("http://galaxyharvester.net/exports/current48.xml");
			in = website.openStream();

			Files.copy(in, Paths.get("data/current48.xml"), StandardCopyOption.REPLACE_EXISTING);

			// Just in-case the user messes with something, we can re-download the XML
			Watcher.createFileWatcher(new File("current48.xml"), () -> {
				try {
					downloadXmls();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return new File("data/current48.xml");
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
		File dir = new File("./data/groups");
		//noinspection ConstantConditions
		for (File file : dir.listFiles()) {
			List<String> resourceGroup = new ArrayList<>();
			resourceGroup.add(file.getName());
			try {
				try (Stream<String> stream = Files.lines(file.toPath())) {
					stream.forEach(resourceGroup::add);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			resourceGroups.put(file.getName(), resourceGroup);
		}
	}
}
